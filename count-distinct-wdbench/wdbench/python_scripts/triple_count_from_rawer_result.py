# note that the script is not the final version, it is just a draft to extract the final report from the rawer/groundtruth result files
import os
import re
from rdflib.plugins.sparql.parser import parseQuery
from rdflib.plugins.sparql.algebra import translateQuery
from rdflib.plugins.sparql.sparql import Bindings, QueryContext
from rdflib.plugins.sparql.parserutils import Expr
from rdflib.term import Identifier, Variable, URIRef
from rdflib.plugins.sparql.algebra import CompValue
from rdflib.util import from_n3
from rdflib.plugins.sparql.algebra import pprintAlgebra
import pprint

def extract_sparql_info_from_report_of_rawer(file_path):
    """Extracts SPARQL query, result, and execution time from a file."""
    with open(file_path, 'r') as file:
        content = file.read()

    query_pattern = re.compile(r"SPARQL query:\s*(SELECT.*?)\n{\?cd->", re.DOTALL)
    #result_pattern = re.compile(r"{\?count->\s*\"(.*?)\"\^\^.*")
    result_pattern = re.compile(r"{\?cd->\s*\"(.*?)\"\^\^.*")
    time_pattern = re.compile(r"Execution time:\s*(\d+) ms")

    query_match = query_pattern.search(content)
    result_match = result_pattern.search(content)
    time_match = time_pattern.search(content)

    if query_match and result_match and time_match:
        query_str = query_match.group(1).strip()
        result = result_match.group(1).strip()
        execution_time = time_match.group(1).strip()
        return {
            "query_str": query_str,
            "result": result,
            "execution_time": execution_time,
        }
    else:
        return None


def extract_and_format_results(file_path):
    #result=[]
    """Processes a result file, extracting relevant information."""
    try:
        sparql_info = extract_sparql_info_from_report_of_rawer(file_path)
        query_str = sparql_info["query_str"]
        parsed_query = parseQuery(query_str)
        #print(parsed_query)
        algebra = translateQuery(parsed_query)
        if "count(distinct" in query_str.lower():
            visitor = CDVisitor()
        elif "count" in query_str.lower():
            visitor = CountVisitor()
        else:
            visitor = Visitor()
        visitor.visit(algebra.algebra)
        triple_count = visitor.nbtriples

        result = sparql_info["result"]
        execution_time = sparql_info["execution_time"]

        file_index = os.path.splitext(os.path.basename(file_path))[0]
        #result.append(file_index,execution_time,result[1:],triple_count)

        return f"{file_index},{float(execution_time)/1000},{float(result[1:])},{int(triple_count)}"
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")
        return None

class Visitor:

    my_query = []
    nbtriples=0

    def visit(self, algebra):
        # Determine the type of the node and visit it
        method_name = 'visit_' + type(algebra).__name__
        if hasattr(algebra, 'name'):
            method_name = 'visit_' + algebra.name
        #print(f"try visiting method_name: {method_name}")
        visitor = getattr(self, method_name, self.generic_visit)
        return visitor(algebra)

    def generic_visit(self, algebra):

        if isinstance(algebra, CompValue):
            #print(f"comp value: {algebra.items()}")
            for key, value in algebra.items():
                if isinstance(value, list):
                    for item in value:
                        self.visit(item)
                elif isinstance(value, CompValue):
                    self.visit(value)
        elif isinstance(algebra, list):
            print(f"list: {algebra}")
            for item in algebra:
                self.visit(item)

    def visit_Project(self, project):
        #print("Visiting Project:", project)
        self.my_query.append("select * WHERE {\n")
        self.visit(project.p)
        self.my_query.append("}")

    def visit_OrderBy(self, order_by):
        # print("Visiting OrderBy:", order_by)
        self.visit(order_by.p)

    def visit_Extend(self, node):
        # print("Visiting Extend:", node)
        self.visit(node.p)

    def visit_LeftJoin(self, node):
        # print("Visiting LeftJoin:", node)
        self.visit(node.p1)
        self.my_query.append("  OPTIONAL {")
        self.visit(node.p2)
        self.my_query.append(" }")

    # Example specific visit method
    def visit_BGP(self, node):
        # print("Visiting BGP:", node)
        self.nbtriples+=len(node.triples)
        triples = "".join(
                    triple[0].n3() + " " + triple[1].n3() + " " + triple[2].n3() + " . \n"
                    for triple in node.triples
                )
        self.my_query.append(triples)
## redefine the visitor to count the number of triples in the query
class  CountVisitor(Visitor):
   def visit_Project(self, project):
        #print("Visiting Project:", project)
        self.my_query.append("select COUNT(*) WHERE {\n")
        self.visit(project.p)
        self.my_query.append("}")

class  CDVisitor(Visitor):
   def visit_Project(self, project):
        #print("Visiting Project:", project)
        vars=project.PV
        self.my_query.append("select (COUNT(DISTINCT %s) as ?cd) WHERE {\n"%(vars[0].n3()))
        self.visit(project.p)
        self.my_query.append("}")
##### for count-distinct ground truth, need to track the number of triples in the query
def extract_gt_count_distinct(file_path):
    with open(file_path, 'r') as file:
        content = file.read()
    # Pattern to Match cd Value
    cd_pattern = r'\[cd="(\d+)"'
    cd_match = re.search(cd_pattern, content)
    cd_value = cd_match.group(1) if cd_match else None

    # Pattern to Match Time in Milliseconds
    ms_pattern = r'Took (\d+) ms'
    ms_match = re.search(ms_pattern, content)
    ms_value = ms_match.group(1) if ms_match else None
    filename = os.path.basename(file_path)
    query_name = filename.split('.')[0]
    return f"{query_name},{ms_value},{cd_value}"


def main():
    report_file_name = "path/to/your/report.csv"
    result_file_directory = "/path/to/your/result/directory"
    with open(report_file_name, 'w') as report_file:
        report_file.write("Query_Index,Execution_Time,Result,Triple_Count\n")
        for file_name in sorted(os.listdir(result_file_directory)):
            if file_name.startswith("query_") and file_name.endswith(".result"):
                file_path = os.path.join(result_file_directory, file_name)
                formatted_row = extract_and_format_results(file_path) # this line for result from rawer
                #formatted_row = extract_gt_count_distinct(file_path) #this line for result from computing GT with embedded
                if formatted_row:
                    report_file.write(formatted_row + "\n")

    print(f"Final report generated: {report_file_name}")

if __name__ == "__main__":
    main()

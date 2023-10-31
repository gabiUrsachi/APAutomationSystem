import json
import random
import sys

file_name = sys.argv[1]
output_file_path = sys.argv[2]

with open(file_name, "r") as f:
    data = json.load(f)

companies = data["APsystem.company"]

with open(output_file_path,"w") as f:
	json.dump(companies,f)


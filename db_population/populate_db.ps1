Write-Host "Cleaning up previous attempts"

rm .\processed-collections\*
rm .\unprocessed-collections\*

Write-Host "generating collections"
.\generators\mgodatagen.exe -f .\generators\companies.json -o .\unprocessed-collections\company-collection.json --prettyprint
.\generators\mgodatagen.exe -f .\generators\po-w-o-proper-company-uuid.json -o .\unprocessed-collections\po-collection.json --prettyprint     
.\generators\mgodatagen.exe -f .\generators\invoice-w-o-proper-company-uuid.json -o .\unprocessed-collections\invoice-collection.json --prettyprint


Write-Host "asigning company uuid for po and invoice transactions"

python utils\inject_uuid_correct_date_a.py .\unprocessed-collections\company-collection.json .\unprocessed-collections\po-collection.json purchaseOrder .\processed-collections\po-collection.json 2
python utils\inject_uuid_correct_date_a.py .\unprocessed-collections\company-collection.json .\unprocessed-collections\invoice-collection.json invoice .\processed-collections\invoice-collection.json 3
python utils\extract_array_from_company_collection.py .\unprocessed-collections\company-collection.json .\processed-collections\company-collection.json



Write-Host "Copying the new data in the database"
mongoimport --db=APsystem --collection=company --uri=mongodb://54.166.80.109:27017  --jsonArray .\processed-collections\company-collection.json 
mongoimport --db=APsystem --collection=purchaseOrder --uri=mongodb://54.166.80.109:27017  --jsonArray .\processed-collections\po-collection.json
mongoimport --db=APsystem --collection=invoice --uri=mongodb://54.166.80.109:27017  --jsonArray .\processed-collections\invoice-collection.json



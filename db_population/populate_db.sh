#!/bin/bash

connection_string="mongodb://localhost:27017"

gen_uuid() {
            uuidgen -r | tr -d "-"
          }

create_companies() {
  if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: insert_data collection_name number_of_elements"
    exit 1
  fi

  collection_name=$1
  number_of_elements=$2

  for (( i=1; i<=$number_of_elements; i++ ))
  do
    id=$(gen_uuid)
    value=$((RANDOM % 1000))

    mongo $connection_string --eval "db.$collection_name.insert(
{id: $id, value: $value})"
  done

  echo "Inserted $number_of_elements elements into $collection_name"
}

drop_collection() {
  if [ -z "$1" ]; then
    echo "Usage: drop_collection collection_name"
    exit 1
  fi

  collection_name=$1

  mongo --eval "db.$collection_name.drop()"

  echo "Dropped $collection_name"
}

if [ $# -lt 2 ]; then
  echo "Usage: sh script.sh function_name arg1 arg2"
  exit 1
fi

function_name=$1

case $function_name in
  insert_data)
    insert_data $2 $3 
    ;;

  create_companies)
    insert_data $2 $3 
    ;;
  drop_collection)
    drop_collection $2
    ;;
  *)
    echo "Invalid function name. Please choose from insert_data or drop_collection."
    exit 1
    ;;
esac

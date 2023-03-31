# Build

There is a build script which helps you to quick start:

## ./build.sh all
Starts the backend and the frontend

## ./build.sh backend
Starts the backend

## ./build.sh frontend
Starts the frontend

## ./build.sh dev-frontend
Starts the frontend with live changes

## import regest file
copy the current regest files from `import` to `docker/backend-tmp/import`
At localhost:8000/backend/ you can login and start the webcli and run the command:
``` 
import regests from cei file /mcr/tmp/import/2023-03-03_regesten_gesamt_red_vi-22.xml and source /mcr/tmp/import/2023-03-03_02_quellen-_und_literaturverzeichnis_regesten_red_viii.csv and manuscript /mcr/tmp/import/2023-03-03_01_hss-verzeichnis_regesten_red_vi.csv
```

## delete old content
For cleanup before importing new version of regest files you can cleanup your data. Stop docker and run:
```
rm -rf docker/backend-logs docker/backend-home docker/backend-data docker/postgres-data docker/solr-data
```


## Backup the cms

The instance of the restore cms should not have any entries in the database. The script will not delete any entries.
```bash
./backup_cms.sh backup usermail userpass http://localhost:8000/cms ./import/cms/

./backup_cms.sh restore usermail userpass http://localhost:8000/cms ./import/cms/
```

# THIS IS THE ONLY ONE LINE TO UPDATE
BASE=/Users/garellano/Desktop/artemisa/db
# **************************************

mkdir -p $BASE/{data,scripts}

DATABASE=$BASE/data
SCRIPTS=$BASE/scripts
ROOT_PASS=garellano

docker stop goodle-db
docker rm goodle-db

docker run -d \
--name goodle-db \
--restart unless-stopped \
-p 3306:3306 \
-v $DATABASE:/var/lib/mysql \
-v $SCRIPTS:/scripts \
-e MYSQL_ROOT_PASSWORD=$ROOT_PASS \
mariadb

echo "Base de datos inicializada !!!"

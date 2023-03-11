
run:
	apkc build
	apkc run

clean:
	apkc clean

download-olam:
	curl https://olam.in/open/enml/olam-enml.csv.zip > olam/olam-enml.csv.zip
	unzip -o olam/olam-enml.csv.zip -d olam
	rm olam/olam-enml.csv.zip olam/README.txt
	mv olam/olam-enml.csv olam/db.csv

build-index:
	cd olam; python3 makedb.py;
	cp olam/mdb.txt res/raw/mdb.txt
	cp olam/wdbd.txt res/raw/wdbd.txt
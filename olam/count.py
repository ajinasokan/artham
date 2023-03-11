db=open("wdb.txt","rb")

count=0
chars=set()
char=db.read(1)

while char != "":
	chars.add(char)
	char=db.read(1)
	count=count+1

chars=sorted(chars)

print chars, len(chars)
#print count
db.close()
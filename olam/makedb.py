csv=open("db.csv","r")
w=open("wdbd.txt","wb")
m=open("mdb.txt","wb")

mc=[u'\t', u'\n', u' ', u'!', u'"', u"'", u'(', u')', u'*', u'+', u',', u'-', u'.', u'/', u'0', u'1', u'2', u'3', u'4', u'5', u'6', u'7', u'8', u'9', u':', u';', u'?', u'A', u'B', u'C', u'D', u'T', u'a', u'b', u'c', u'd', u'e', u'f', u'g', u'h', u'i', u'j', u'l', u'm', u'n', u'o', u'p', u'r', u's', u't', u'u', u'v', u'w', u'x', u'y', u'z', u'|', u'\xa0', u'\xf5', u'\xf6', u'\xf8', u'\u0d02', u'\u0d03', u'\u0d05', u'\u0d06', u'\u0d07', u'\u0d08', u'\u0d09', u'\u0d0a', u'\u0d0b', u'\u0d0e', u'\u0d0f', u'\u0d10', u'\u0d12', u'\u0d13', u'\u0d14', u'\u0d15', u'\u0d16', u'\u0d17', u'\u0d18', u'\u0d19', u'\u0d1a', u'\u0d1b', u'\u0d1c', u'\u0d1d', u'\u0d1e', u'\u0d1f', u'\u0d20', u'\u0d21', u'\u0d22', u'\u0d23', u'\u0d24', u'\u0d25', u'\u0d26', u'\u0d27', u'\u0d28', u'\u0d2a', u'\u0d2b', u'\u0d2c', u'\u0d2d', u'\u0d2e', u'\u0d2f', u'\u0d30', u'\u0d31', u'\u0d32', u'\u0d33', u'\u0d34', u'\u0d35', u'\u0d36', u'\u0d37', u'\u0d38', u'\u0d39', u'\u0d3e', u'\u0d3f', u'\u0d40', u'\u0d41', u'\u0d42', u'\u0d43', u'\u0d46', u'\u0d47', u'\u0d48', u'\u0d4a', u'\u0d4b', u'\u0d4c', u'\u0d4d', u'\u0d57', u'\u0d6a', u'\u0d7a', u'\u0d7b', u'\u0d7c', u'\u0d7d', u'\u0d7e', u'\u200b', u'\u200c', u'\u200d', u'\u2026', u'\u25e6', u'W']

entry={}

line=csv.readline()
line=csv.readline()

while line != "":
	if len(line.split('	')) < 4:
		line = csv.readline()
		continue
	wid, word, wtype, mean = line.split('	')
	word=word.lower().strip()
	if word in entry:
		entry[word]+="\n" + wtype + "	" + mean.strip()
	else:
		entry[word]=wtype + "	" + mean.strip()
	line = csv.readline()

entry['levidrome'] = 'n	' + 'ഒരു വാക്കിനെ തിരിച്ചെഴുതുമ്പോൾ രൂപപ്പെടുന്ന മറ്റൊരു പുതിയ വാക്ക്.'
entry['meraki'] = 'a	' + 'അഭിനിവേശത്തോടെയും, അതീവ ശ്രദ്ധയോടെയും, സമ്പൂർണ്ണ സമർപ്പണത്തോടെയും ചെയ്യുന്ന കർമ്മം'

print(entry["apple"])

for key in sorted(entry.keys()):
	means = entry[key].split('\n')
	meanset = set(means)
	if len(means) != len(meanset):
		print("Duplicates in ", key)
		#entry[key] = '\n'.join(sorted(list(meanset)))
		entry[key] = '\n'.join(list(meanset))

print("\nuse following map index inside Database.java -> initMaps()\n")

mpos=0
dbpos=0
index={}
for alpha in [ord('.')] + list(range(ord('a'),ord('z')+1)):
	print("map.put('" + chr(alpha) + "', " + str(dbpos) + ");")
	index[chr(alpha)]=dbpos

	mpos=0
	for key in sorted(entry.keys()):
		if key[0:1] == chr(alpha):
			w.write((key + "	" + str(mpos) + "\n").encode())

			for char in entry[key]:
				m.write(bytes([mc.index(char)]))
				mpos+=1
				dbpos+=1

			m.write(bytes([127]))
			mpos+=1
			dbpos+=1
	
m.close()
w.close()
csv.close()

w=open("wdbd.txt","rb")
data=w.read()
w.close()


wc=set()

for char in data:
	wc.add(char)
	
wc=sorted(wc)

print(wc)

bits=""

#[^a-z0-9 	\n.\-\'\(\),\!\?\:\'\/\&]

for i in range(len(data)):
	char = data[i]
	x=wc.index(char)
	bits=bits+format(x, '06b')

bits=bits + ('0' * (8 - (len(bits) % 8)))

w=open("wdb.txt","wb")

for i in range(len(bits)//8 - 1):
	w.write(bytes([int(bits[i*8:i*8+8],2)]))

w.close()
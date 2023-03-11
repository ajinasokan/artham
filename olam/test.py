words=open("wdb.txt","rb")

wc = ['\t', '\n', ' ', '!', '&', "'", '(', ')', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', '?', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\xa9', '\xc3']

data=words.readline()
bits=""

for char in data:
	x=ord(char)
	bits=bits+format(x, '08b')
	print bits

len(bits) - (len(bits) % 6)

bits=bits[0:len(bits) - (len(bits) % 6)]



data=""
for i in xrange(len(bits)/6 - 1):
	data+=wc[int(bits[i*6:i*6+6],2)]

words.close()
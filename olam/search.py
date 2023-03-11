import os
def addToClipBoard(text):
    command = 'echo ' + text.strip() + '| clip'
    os.system(command)

"""
words=open("wdb.txt","rb")

wc = ['\t', '\n', ' ', '!', '&', "'", '(', ')', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', '?', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\xa9', '\xc3']

data=words.read()
bits=""

for char in data:
	x=ord(char)
	bits=bits+format(x, '08b')

len(bits) - (len(bits) % 6)

bits=bits[0:len(bits) - (len(bits) % 6)]

data=""
for i in xrange(len(bits)/6 - 1):
	data+=wc[int(bits[i*6:i*6+6],2)]

words.close()
"""
data=open("wdbd.txt","rb")
dictionary = data.read().split("\n")
data.close()

word=raw_input()
left=0
right=len(dictionary)-1

while left <= right:
    middle = (left+right)//2
    current=dictionary[middle].split("	")[0]
    if current == word:
        break
    elif left >= right:
        middle+=1
        break
    elif current < word:
        left = middle+1
    elif current > word:
        right = middle - 1

for i in xrange(5):
	if i+middle < len(dictionary):
		print dictionary[i+middle]

mc=[u'\t', u'\n', u' ', u'!', u'"', u"'", u'(', u')', u'*', u'+', u',', u'-', u'.', u'/', u'0', u'1', u'2', u'3', u'4', u'5', u'6', u'7', u'8', u'9', u':', u';', u'?', u'A', u'B', u'C', u'D', u'T', u'a', u'b', u'c', u'd', u'e', u'f', u'g', u'h', u'i', u'j', u'l', u'm', u'n', u'o', u'p', u'r', u's', u't', u'u', u'v', u'w', u'x', u'y', u'z', u'|', u'\xa0', u'\xf5', u'\xf6', u'\xf8', u'\u0d02', u'\u0d03', u'\u0d05', u'\u0d06', u'\u0d07', u'\u0d08', u'\u0d09', u'\u0d0a', u'\u0d0b', u'\u0d0e', u'\u0d0f', u'\u0d10', u'\u0d12', u'\u0d13', u'\u0d14', u'\u0d15', u'\u0d16', u'\u0d17', u'\u0d18', u'\u0d19', u'\u0d1a', u'\u0d1b', u'\u0d1c', u'\u0d1d', u'\u0d1e', u'\u0d1f', u'\u0d20', u'\u0d21', u'\u0d22', u'\u0d23', u'\u0d24', u'\u0d25', u'\u0d26', u'\u0d27', u'\u0d28', u'\u0d2a', u'\u0d2b', u'\u0d2c', u'\u0d2d', u'\u0d2e', u'\u0d2f', u'\u0d30', u'\u0d31', u'\u0d32', u'\u0d33', u'\u0d34', u'\u0d35', u'\u0d36', u'\u0d37', u'\u0d38', u'\u0d39', u'\u0d3e', u'\u0d3f', u'\u0d40', u'\u0d41', u'\u0d42', u'\u0d43', u'\u0d46', u'\u0d47', u'\u0d48', u'\u0d4a', u'\u0d4b', u'\u0d4c', u'\u0d4d', u'\u0d57', u'\u0d6a', u'\u0d7a', u'\u0d7b', u'\u0d7c', u'\u0d7d', u'\u0d7e', u'\u200b', u'\u200c', u'\u200d', u'\u2026', u'\u25e6']
index={'.': 0, 'a': 40, 'c': 432905, 'b': 245952, 'e': 1015491, 'd': 783010, 'g': 1376098, 'f': 1153839, 'i': 1643717, 'h': 1493497, 'k': 1823744, 'j': 1791385, 'm': 1983567, 'l': 1856611, 'o': 2224885, 'n': 2158326, 'q': 2692632, 'p': 2324939, 's': 2961363, 'r': 2717335, 'u': 3836726, 't': 3585866, 'w': 3977538, 'v': 3914302, 'y': 4091376, 'x': 4088638, 'z': 4103245}

pos=index[dictionary[middle][0:1]] + int(dictionary[middle].split("	")[1])

print pos

mean=open("mdb.txt","rb")
mean.seek(pos)

out=""
char=mean.read(1)
while char != chr(127):
	x=mc[ord(char)]
	out+=x.encode('utf-8')
	char=mean.read(1)
mean.close()

o=open("out.txt","wb")
o.write(out)
o.close()
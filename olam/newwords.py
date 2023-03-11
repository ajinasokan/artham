old=[item.split('\t')[0] for item in open("wdbd.txt").read().split('\n')]
new=[item.split('\t')[0] for item in open("wdbdn.txt").read().split('\n')]

for item in new:
	if item not in old:
		print item
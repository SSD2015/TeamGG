import requests, re
from bs4 import BeautifulSoup

print 'username,name,organization,role'

for group in file("groups.txt"):
	group = group.strip()
	data = requests.get("https://github.com/SSD2015/{}".format(group)).text
	dom = BeautifulSoup(data)
	for table in dom.find_all('table'):
		# print tables.get_text()
		if re.search(r'5[46]1054[0-9]{4}', table.get_text()):
			rows = table.find('tbody').find_all('tr')
			for row in rows:
				data = {'name': '', 'github': '', 'id': ''}
				for td in row.find_all('td'):
					txt = td.get_text()
					if txt.startswith('@'):
						data['github'] = txt[1:]
					elif txt.startswith('5'):
						data['id'] = txt
					else:
						data['name'] = txt
				print 'b{id},{name},{group},VOTER'.format(group=group, **data)
			break
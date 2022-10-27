
from re import T
from time import sleep
import requests
import lxml.html
import json
# 请求头
retObj=[]
headers = {
    'user-agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:27.0)  \
                    Gecko/20100101 Firefox/27.0',
    'Accept': 'text/html,application/xhtml+xml,application/xml; \
                q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8, \
                application/signed-exchange;v=b3;q=0.9',
    'accept-encoding': 'gzip, deflate, br',
    'accept-language': 'zh-CN,zh;q=0.9',
    'cache-control': 'max-age=0',
    'Host': 'kopernio.com',
    'sec-fetch-dest': 'empty',
    'sec-fetch-mode': 'cors',
    'sec-fetch-site': 'same-origin',
    'x-kopernio-plugin-version': '0.12.26'
}

# ~227
url = 'https://segmentfault.com/tags?sort=hottest&page='

for page in range(1,228):
    print("page",page)
    sleep(0.1)
    res = requests.get(url + str(page),headers,verify=False)
    res.encoding = res.apparent_encoding
    html_doc = res.text
    selector=lxml.html.fromstring(html_doc)
    tagNmaeList = selector.xpath('//*[@id="root"]/div[4]/div[2]/div/div/div/a')
    tagInfoList = selector.xpath('//*[@id="root"]/div[4]/div[2]/div/div/div/p')
    for i in range(0,tagNmaeList.__len__()):
        tag = {
            "name":tagNmaeList[i].text,
            "introduction":tagInfoList[i].text
        }
        retObj.append(tag)
    print("-------------------------")

print(retObj)
filename='/home/dwsy/code/segmentfaultTag.json'
with open(filename,'w') as file_obj:
  json.dump(retObj,file_obj)
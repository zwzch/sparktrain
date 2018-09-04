#coding = UTF-8
import random

import time

url_path = ['class/112.html',
            'class/128.html',
            'class/145.html',
            'class/146.html',
            'class/141.html',
            'class/112.html',
            'class/128.html',
            'class/145.html',
            'class/146.html',
            'class/141.html',
            'course/list',
            'learn/821']



ip_slices = [132,156,124,10,167,30,80,187,72,87,98,60,20,45]

# referers
http_referers = [
    "http://www.baidu.com/s?wd={query}",
    "https://www.sogou.com/web?query={query}",
    "http://cn.bing.com/search?q={query}",
    "https://search.yahoo.com/search?p={query}",
]

search_keyword = [
    "Spark SQL实战",
    "Hadoop基础",
    "Storm实战",
    "Spark Streaming实战",
    "大数据面试"
]

status_codes = ["200","404","500"]

def sample_url():
    return random.sample(url_path, 1)[0]

def sample_ip():
    slice = random.sample(ip_slices, 4)
    return ".".join([str(item) for item in slice])


def sample_status_code():
    return random.sample(status_codes, 1)[0]


def sample_referer():
    refer_str = random.sample(http_referers, 1)
    query_str = random.sample(search_keyword, 1)
    result = refer_str[0].format(query=query_str[0])
    if random.uniform(0,1) > 0.2:
        return "-"
    else:
        return result



def generate_log(count = 10):
	time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())

	f = open("/home/zw/PycharmProjects/Demo1/access.log","w+")

	for i in range(0, count):
		query_log = "{ip}\t{local_time}\t\"GET /{url} HTTP/1.1\"\t{status_codes}\t{referer}".format(url=sample_url(), ip=sample_ip(), referer=sample_referer(), status_codes=sample_status_code(), local_time=time_str)
		print (query_log)
		f.write(query_log + "\n")

def main():
    generate_log(5)
if __name__ == '__main__':
    main()
import math

import json
import requests

# def get_distance(origin_lng, origin_lat, destination_lng, destination_lat):
#     # url = 'http://api.map.baidu.com/directionlite/v1/walking?'
#     # params = {'origin': origin,
#     #           'destination': destination,
#     #           'ak': 'xVYwiCXy5h9d4oPpRG6rBrwjKaYNUF2G'
#     #           }
#     url = 'http://api.map.baidu.com/directionlite/v1/walking?origin={0},{1}&destination={2},{3}&ak={4}'.format(
#         origin_lng, origin_lat, destination_lng, destination_lat, 'xVYwiCXy5h9d4oPpRG6rBrwjKaYNUF2G')
#     try:
#         response = requests.get(url)
#         jd = json.loads(response.text)
#         return jd['route']['paths'][0]['distance']
#     except:
#         return 0


# lat lon - > distance

# 计算经纬度之间的距离，单位为千米
from models import Address

EARTH_REDIUS = 6378.137


def rad(d):
    return d * math.pi / 180.0


# 获取两点间的距离*1000，再将其保留小数点后两位
def getDistance(lat1, lng1, lat2, lng2):
    radLat1 = rad(lat1)
    radLat2 = rad(lat2)
    a = radLat1 - radLat2
    b = rad(lng1) - rad(lng2)
    s = 2 * math.asin(
        math.sqrt(math.pow(math.sin(a / 2), 2) + math.cos(radLat1) * math.cos(radLat2) * math.pow(math.sin(b / 2), 2)))
    s = round(s * EARTH_REDIUS * 1000, 2)
    return s


# 遍历查询结果
def piont_distance(dataAddress, lng, lat):
    list1 = []
    for item in dataAddress:
        dic = dict()
        dic['lng'] = item.longitude
        dic['lat'] = item.latitude
        list1.append(dic)
    # return getDistance(float(lng), float(lat), list1[0].get('lng'), list1[0].get('lat'))
    dic = dict()
    # if len(list1) == 1:
    #     if getDistance(float(lng), float(lat), list1[0].get('lng'), list1[0].get('lat')) <= 10:
    #         dic['lng'] = list1[0].get('lng')
    #         dic['lat'] = list1[0].get('lat')
    #         return dic

    if len(list1) != 0:
        min_distance = 0
        count = 0
        for item in list1:
            distance = getDistance(float(lng), float(lat), item.get('lng'), item.get('lat'))
            if distance <= 50:
                if count == 0:
                    min_distance = distance
                    dic['lng'] = item.get('lng')
                    dic['lat'] = item.get('lat')
                if distance <= min_distance:
                    dic['lng'] = item.get('lng')
                    dic['lat'] = item.get('lat')
                count += 1
    return dic
    # else:
    #     dic['err'] = 'error'
    #     return dic


# 将符合条件的描述查询出来
def select_describe(dataAddress, lng, lat):
    dic = piont_distance(dataAddress, lng, lat)
    request = dict()
    if dic:
        lng = dic.get('lng')
        lat = dic.get('lat')
        address = Address.query.filter_by(longitude=lng, latitude=lat).first()
        request['inf'] = address.description
        return request
    request['inf'] = "您当前距离目标太远"
    return request

import math

# 计算经纬度之间的距离，单位为千米
from models import Address, User

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
    dic = dict()
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


# 将小于500米内同一方向的marker筛选出来
def marker_position(dataAddress, lng, lat):
    marker_list = []
    if len(dataAddress) != 0:
        for item in dataAddress:
            distance = getDistance(float(lng), float(lat), float(item.longitude), float(item.latitude))
            if distance <= 500:
                dic = dict()
                dic['longitude'] = item.longitude
                dic['latitude'] = item.latitude
                marker_list.append(dic)
    return marker_list


# 将符合条件的描述查询出来
def select_describe(dataAddress, lng, lat, flag):
    if flag == '1':
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
    elif flag == 'marker':
        marker_list = marker_position(dataAddress, lng, lat)
        if marker_list:
            return marker_list
        request = dict()
        request['inf'] = "没有数据"
        request_list = [request]
        return request_list

# 配置flask配置对象中键：SQLALCHEMY_DATABASE_URI
SQLALCHEMY_DATABASE_URI = 'mysql+pymysql://root:944343631Hyb@8.135.61.184:3306/CommentaryWeb'
# 配置flask配置对象中键: SQLALCHEMY_COMMIT_TEARDOWN,设置为True,应用会自动在每次结束后提交数据库中的变动
SQLALCHEMY_COMMIT_TEARDOWN = True
SQLALCHEMY_TRACK_MODIFICATIONS = True
SQLALCHEMY_COMMIT_ON_TEARDOWN = True
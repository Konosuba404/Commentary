from flask import Flask
from models import db

def create_app():
    # instance_relative_config告诉配置文件是相对于instance folder文件夹的
    app = Flask(__name__, instance_relative_config=True)
    # 加入数据库配置
    app.config.from_object('dbconfig')
    db.init_app(app)

    return app


if __name__ == '__main__':
    create_app().run()

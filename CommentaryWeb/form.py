from flask_wtf import Form
from werkzeug.security import check_password_hash
from wtforms import StringField, PasswordField, SubmitField, validators, FloatField
from wtforms.validators import DataRequired, Length, EqualTo, ValidationError
from models import User
from sqlalchemy.sql import exists


# 注册表单
class RegisterForm(Form):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', [
        validators.DataRequired(),
        validators.length(min=8, max=10, message='密码长度必须大于%(min)d且小于%(max)d')
    ])
    RePassword = PasswordField('Confirm Password', validators=[DataRequired(), EqualTo('password', message='密码必须一致')])
    submit = SubmitField('注册')

    # 检验用户名是否存在
    @staticmethod
    def validate_username(self, username):
        if User.query.filter(exists().where(User.username == username.data)).scalar():
            raise ValidationError(u'用户名已被注册！')


# 登录表单
class LoginForm(Form):
    username = StringField('Username', validators=[DataRequired()])
    password = PasswordField('Password', validators=[
        DataRequired(),
        Length(min=8, max=10, message='密码长度必须大于%(min)d且小于%(max)d')])
    # recaptcha = RecaptchaField()
    submit = SubmitField('登录')

    # 检查用户是否存在
    @staticmethod
    def validate_username(self, username):
        if not User.query.filter(exists().where(User.username == username.data)).scalar():
            raise ValidationError(u'当前用户未注册')

    # # 检查密码是否正确
    # @staticmethod
    # def validate_password(username, password):
    #     user = User.query.filter_by(username=username).first()
    #     if not check_password_hash(user.password, password.data):
    #         raise ValidationError(u'密码错误')


# # 提交表单
# class SubmitTable(Form):
#     Longitude = FloatField('Longitude', validators=[DataRequired()])
#     Latitude = FloatField('Latitude', validators=[DataRequired()])
#     Description = StringField('Description', validators=[DataRequired()])
#     submit = SubmitField('删除')

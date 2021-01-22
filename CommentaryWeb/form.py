from flask_wtf import Form, RecaptchaField
from wtforms import StringField, PasswordField, SubmitField
from wtforms.validators import DataRequired, Length, EqualTo, ValidationError
from models import User
from sqlalchemy.sql import exists


# 注册表单
class RegisterForm(Form):
    username = StringField('username', validators=[DataRequired()])
    password = PasswordField('password', validators=[DataRequired(), Length(min=8, max=10)])
    RePassword = PasswordField('RePassword', validators=[DataRequired(), EqualTo('password')])
    submit = SubmitField('注册')

    # 检验用户名是否存在
    @staticmethod
    def validate_username(username):
        if User.query(exists().where(User.username == username.data)).scalar():
            raise ValidationError(u'用户名已被注册！')


# 登录表单
class LoginForm(Form):
    username = StringField('username', validators=[DataRequired()])
    password = StringField('password', validators=[DataRequired(), Length(min=8, max=10)])
    recaptcha = RecaptchaField()
    submit = SubmitField('登录')
    register = SubmitField('注册')

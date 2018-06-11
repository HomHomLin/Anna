#林宏弘测试用例
#插入模式
-switch {
# all;
 custom;
}

#包含插入
-include {
  com.meetyou.aop.demo.AnnaInject onClick;
  com.meetyou.aop.demo.AnnaInject onLongClick;
  com.meetyou.aop.demo.AnnaInject2 onClick;
  com.meetyou.aop.demo.AnnaInject2 onLongClick;
}
#
#-userinfo {
# name lili yuxiang;
# sex nv nan;
#}
-insert method{
#   com.meetyou.aop.assassin.MainActivity*onCreate;
  **.all;
#  *.<init>;
#  *.onClick;
}

-replace method{
#  *.show;
}

-receiver {
  com.meetyou.aop.assassin.TestDelegate;
}


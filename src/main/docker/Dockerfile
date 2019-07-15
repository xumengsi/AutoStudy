FROM centos:7

MAINTAINER xumengsi <xums@cu-sc.com>

ENV JDK jdk1.8.0_191

ENV APPLICATION_NAME autostudy-0.0.1-SNAPSHOT.jar

ENV GOOGLE_CHROME google-chrome-stable_current_x86_64.rpm

ENV CHROME_DRIVER chromedriver

ENV APP_RUN run.sh

ADD $JDK /opt/jdk1.8.0_191

ADD $APPLICATION_NAME /opt/

ADD $GOOGLE_CHROME /opt/google-chrome-stable_current_x86_64.rpm

ADD $CHROME_DRIVER /opt/chromedriver

ADD $APP_RUN /opt/run.sh

ENV JAVA_HOME=/opt/jdk1.8.0_191

ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

ENV PATH $PATH:$JAVA_HOME/bin

RUN yum install pax* -y && \
    yum install redhat-lsb* -y && \
    yum install libXss* -y && \
    yum install libappindicator-gtk3 -y && \
    yum install liberation-fonts -y && \
    yum install wget -y && \
    yum install initscripts -y
#    rpm -ivh /opt/google-chrome-stable_current_x86_64.rpm


RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo 'Asia/Shanghai' >/etc/timezone

EXPOSE 12800

# run.sh
CMD ["sh", "-c", "cd /opt; sh run.sh $APPLICATION_NAME"]
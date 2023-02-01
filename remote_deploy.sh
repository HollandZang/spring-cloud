#!/bin/bash

# ssh 连接服务器
SSH_IP=114.115.212.83
SSH_PORT=22

# 部署路径
DEPLOY_PATH=/project/cloud

# 声明打包内容为多模块项目
MULTI_MODULE=true
# 声明多模块项目额外的需要安装的公共模块,额外打包的模块在 单独依赖 模式下每次都会重新上传
#MULTI_COMMON_MODULES=common,common-nacos,common-hadoop,common-kafka,common-redis
MULTI_COMMON_MODULES=common,common-nacos,common-hadoop,common-kafka
MULTI_COMMON_MODULES_UPLOAD=common-0.0.1-SNAPSHOT.jar,common-kafka-1.0.jar,common-nacos-0.0.1-SNAPSHOT.jar,common-redis-1.0.jar
# 多项目共用libs文件夹

BASE_PATH=$(dirname "$0")
JAR_NAME=""

readFile2Arr4Line() {
  file_name=$1
  grep -v '^ *#' < "$BASE_PATH/$file_name" | while IFS= read -r line
  do
    echo "$line"
  done
}

# 查看 已上传的lib包 发现未上传的才上传
uploadLibs() {
  if [ -f "$BASE_PATH"/remote_deploy_libs.record ];
    then
      uploadedArr=( $(readFile2Arr4Line remote_deploy_libs.record) )
    else
      touch "$BASE_PATH"/remote_deploy_libs.record
      uploadedArr=()
  fi

  cd target/libs || exit
  for file in *
  do
    isUploaded=false
    # 判断是否已上传
    for i in "${!uploadedArr[@]}";
    do
      if [ "$file" = "${uploadedArr[$i]}" ];then
        #echo "已经上传lib包：$file"
        isUploaded=true
        unset 'uploadedArr[$i]'
        break
      fi
    done
    # 判断是否是指定打包的，需要更新
    for common_module_name in ${MULTI_COMMON_MODULES_UPLOAD//,/ }
    do
      if [ "$file" = "$common_module_name" ];then
        echo -e "\033[36;49m 正在更新lib包：$file \033[0m"
        isUploaded=false
        break
      fi
    done
    if [ false = "$isUploaded" ];then
        #echo -e "\033[36;49m 正在上传lib包：$file \033[0m"
        scp -P $SSH_PORT "$file" root@$SSH_IP:$DEPLOY_PATH/libs/"$file"
        echo "$file" >> "$BASE_PATH"/remote_deploy_libs.record
    fi
  done
  cd "$BASE_PATH" || exit
}

uploadJar() {
  cd target || exit
  for file in *
  do
    if test -f "$file" && [[ $file == *.jar ]];then
      JAR_NAME=$file
      # todo 去备份
      echo -e "\033[36;49m 正在备份jar包：$file \033[0m"
      #ssh root@$SSH_IP -p $SSH_PORT "cd $DEPLOY_PATH;"

      echo -e "\033[36;49m 正在上传jar包：$file \033[0m"
      scp -P $SSH_PORT "$file" root@$SSH_IP:$DEPLOY_PATH/"$file"
    fi
  done
  cd "$BASE_PATH" || exit
}

check_env() {
  echo -e "\033[31;40m> 环境检测开始 \033[0m"
  # 加载用户环境
  if [ -f ~/.bash_profile ];then
      source ~/.bash_profile
      echo -e "\033[36;49m 加载用户环境 ~/.bash_profile \033[0m"
  fi

  # 检测环境变量
  if [ -n "$MAVEN_HOME" ];
    then
      echo -e "\033[36;49m MAVEN_HOME=$MAVEN_HOME \033[0m"
    else
      echo -e "\033[31;40m> 未检测到MAVEN_HOME！\033[0m" >&2
      exit 1
  fi

  # 检测远程环境
  ssh root@$SSH_IP -p $SSH_PORT "mkdir -p $DEPLOY_PATH/libs"
  echo -e "\033[31;40m> 环境检测结束 \033[0m"
}

pkg_and_upload() {
  CMD_PACKAGE="mvn clean package"
  # 根据参数选择不同 pom 文件
  echo -e "\033[36;49m 1(默认). 含依赖打包\n 2. 单独依赖打包\033[0m"
  while : ; do
    read -rp "> 选择打包模式: " module
    case $module in
    "" | 1)
      echo -e "\033[31;40m> 使用 含依赖打包 模式 \033[0m"
      # CMD_PACKAGE+=" -f pom.xml"
      break;;
    2)
      echo -e "\033[31;40m> 使用 单独依赖打包 模式 \033[0m"
      CMD_PACKAGE+=" -f detached_pom.xml"
      break;;
    *)
      echo -e "\033[31;40m> 不被接受的参数 [$module] \033[0m";;
    esac
  done

  if [ true = "$MULTI_MODULE" ];
    then
      # 多模块
       # 可以指定打包子模块
    #  read -erp "> 是否指定打包的子模块: " module
    #  if [ -z "$module" ];
    #    then
    #      "mvn clean install"
    #    else
    #      "mvn clean install -pl $module -am"
    #  fi
      while : ; do
        read -erp "> 是否指定打包的子模块: " module_name
        if [ -n "$module_name" ];then
          break
        fi
      done

      # 额外的公共模块打包
      for common_module_name in ${MULTI_COMMON_MODULES//,/ }
      do
        echo -e "\033[31;40m> 远程部署-mvn打包-额外的公共模块打包-$common_module_name \033[0m"
        cd "$common_module_name" || exit
        if ! mvn clean install; then
          echo -e "\033[31;40m> 模块打包失败[$common_module_name]！\033[0m" >&2
          exit 2
        fi
        cd "$BASE_PATH" || exit
      done

      echo -e "\033[31;40m> 远程部署-mvn打包-指定子模块打包-$module_name \033[0m"
      cd "$module_name" || exit
      if ! $CMD_PACKAGE; then
        echo -e "\033[31;40m> 模块打包失败[$module_name]！\033[0m" >&2
        exit 2
      fi
      uploadLibs
      cd "$module_name" || exit
      uploadJar
    else
      # 单体项目
      if ! $CMD_PACKAGE; then
        echo -e "\033[31;40m> 模块打包失败[$module_name]！\033[0m" >&2
        exit 2
      fi
      #uploadLibs
      uploadJar
  fi
}

restart() {
# 指定libs方式 java -Dloader.path=libs/ -jar gateway-0.0.1-SNAPSHOT.jar
ssh root@$SSH_IP -p $SSH_PORT bash -c "'
cd $DEPLOY_PATH
# 启动备份，用备份端口
  # 端口从tartget/classes/配置文件获取
  # 根据类型yml、properties执行不同的解析
  # 优先根据spring.profiles.active来查端口
# 停用已启动的
pid=$(ps -ef|grep "$JAR_NAME"|grep -v grep | awk '{print $2}')
if [ -n $pid ]
  then
    echo pid进程 :$pid
    kill -9 $pid
    echo kill $JAR_NAME :$pid
 else
    echo 进程没有启动
fi
# 启动最新包
java -Dloader.path=libs/ -jar gateway-0.0.1-SNAPSHOT.jar
# 停用备份
'"
}

main() {
  check_env
  echo -e "\033[31;40m> 远程部署开始 \033[0m"
  pkg_and_upload
#  restart
  echo -e "\033[31;40m> 远程部署结束 \033[0m"
}

#main

JAR_NAME=gateway-0.0.1-SNAPSHOT.jar
ssh root@$SSH_IP -p $SSH_PORT bash -c "
cd $DEPLOY_PATH
# 启动备份，用备份端口
  # 端口从tartget/classes/配置文件获取
  # 根据类型yml、properties执行不同的解析
  # 优先根据spring.profiles.active来查端口
# 停用已启动的

ps -ef|grep $JAR_NAME|grep -v grep|awk '{print \$2}'

pid=\$(ps -ef|grep $JAR_NAME|grep -v grep|awk '{print \$2}')
echo pid=\$pid
if [ -n \$pid ]
  then
    echo pid进程 :\$pid
    kill -9 \$pid
    echo kill $JAR_NAME :\$pid
 else
    echo 进程没有启动
fi
# 启动最新包
#java -Dloader.path=libs/ -jar gateway-0.0.1-SNAPSHOT.jar
# 停用备份
"

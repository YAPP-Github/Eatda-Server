#!/bin/bash
echo ECS_CLUSTER=${ecs_cluster_name} >> /etc/ecs/ecs.config

mkdir -p /home/ec2-user/logs/backup
mkdir -p /home/ec2-user/mysql
mkdir -p /home/ec2-user/scripts

chown -R ec2-user:ec2-user /home/ec2-user/logs /home/ec2-user/mysql /home/ec2-user/scripts

aws s3 cp s3://eatda-storage-dev/scripts/app-backup-dev-logs.sh /home/ec2-user/scripts/app-backup-dev-logs.sh
chmod +x /home/ec2-user/scripts/app-backup-dev-logs.sh

aws s3 cp s3://eatda-storage-dev/scripts/mysql-backup.sh /home/ec2-user/scripts/mysql-backup.sh
chmod +x /home/ec2-user/scripts/mysql-backup.sh

yum install -y cronie
systemctl enable crond
systemctl start crond

until systemctl is-active --quiet crond; do
  sleep 1
done

sudo chown -R ec2-user:ec2-user /home/ec2-user/mysql

sudo dnf install -y mariadb105

(
  sudo crontab -u ec2-user -l 2>/dev/null || true
  echo "0 0 * * 0 /home/ec2-user/scripts/app-backup-dev-logs.sh >> /home/ec2-user/logs/backup/app-backup.log 2>&1"
  echo "30 0 * * 0 /home/ec2-user/scripts/mysql-backup.sh >> /home/ec2-user/logs/backup/mysql-backup.log 2>&1"
) | sudo crontab -u ec2-user -


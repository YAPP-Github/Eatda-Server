#!/bin/bash
echo ECS_CLUSTER=${ecs_cluster_name} >> /etc/ecs/ecs.config

fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab

/bin/mkdir -p /home/ec2-user/logs/eatda

aws s3 cp s3://eatda-storage-dev/scripts/app-backup-dev-logs.sh /home/ec2-user/logs/eatda/app-backup-dev-logs.sh
chmod +x /home/ec2-user/logs/eatda/app-backup-dev-logs.sh

aws s3 cp s3://eatda-storage-dev/scripts/mysql-backup.sh /home/ec2-user/eatda/mysql/mysql-backup.sh
chmod +x /home/ec2-user/eatda/mysql/mysql-backup.sh

yum install -y cronie
systemctl enable crond
systemctl start crond

until systemctl is-active --quiet crond; do
  sleep 1
done

(crontab -l 2>/dev/null; echo "0 0 * * 0 /home/ec2-user/logs/eatda/app-backup-dev-logs.sh >> /var/log/app-backup.log 2>&1") | crontab -
(crontab -l 2>/dev/null; echo "30 0 * * 0 /home/ec2-user/eatda/mysql/mysql-backup.sh >> /var/log/mysql-backup.log 2>&1") | crontab -

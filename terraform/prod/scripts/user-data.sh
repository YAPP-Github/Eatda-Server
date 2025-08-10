#!/bin/bash
echo ECS_CLUSTER=${ecs_cluster_name} >> /etc/ecs/ecs.config

fallocate -l 2G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab

mkdir -p /home/ec2-user/logs/backup
mkdir -p /home/ec2-user/scripts

chown -R ec2-user:ec2-user /home/ec2-user/logs /home/ec2-user/scripts

aws s3 cp s3://eatda-storage-prod/scripts/app-backup-prod-logs.sh /home/ec2-user/scripts/app-backup-prod-logs.sh
chmod +x /home/ec2-user/scripts/app-backup-prod-logs.sh

yum install -y cronie
systemctl enable crond
systemctl start crond

until systemctl is-active --quiet crond; do
  sleep 1
done

(
  sudo crontab -u ec2-user -l 2>/dev/null || true
    echo "0 0 * * 0 /home/ec2-user/scripts/app-backup-prod-logs.sh >> /home/ec2-user/logs/backup/app-backup.log 2>&1"
) | sudo crontab -u ec2-user -

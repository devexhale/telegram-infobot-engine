gradle :bot-template:bootRun

docker compose -p bot_template_devstack -f ./docker-compose-dev.yml up --build -d

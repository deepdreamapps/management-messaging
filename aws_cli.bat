aws ecs register-task-definition --cli-input-json file://taskdef.json
aws ecs create-service --cli-input-json file://servicedef.json

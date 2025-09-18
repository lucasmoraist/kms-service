# KMS Service
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)

## Descrição
Este serviço tem como objetivo explorar o AWS KMS, aprendendo boas práticas de assinatura e verificação de certificados de 
forma prática e aplicada.

---
## How to

### Criando KMS
1. Acessar [AWS KMS](https://us-east-1.console.aws.amazon.com/kms/home?region=us-east-1#/kms/keys)
2. Clicar em `Criar chave`
3. Tipo de chave: `Assimétrica`
4. Uso da chave: `Assinar e verificar`
5. Especificação da chave: `RSA_2048`
6. Clicar em `Próximo`
7. Preencher o `Alias`
8. Ir para a etapa final e clicar em `Concluir`


### Criando IAM
#### Permissões para o IAM
Em `Resource` deve ser trocado:
- Trocar `us-east-1` pela região do kms
- Trocar `111122223333` pelo ID da conta da AWS
- Trocar `abcd-1234-efgh-5678` pelo ID da chave KMS
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "kms:Encrypt",
        "kms:Decrypt",
        "kms:ReEncrypt*",
        "kms:GenerateDataKey*",
        "kms:DescribeKey",
        "kms:Sign",
        "kms:Verify"
      ],
      "Resource": [
        "arn:aws:kms:us-east-1:111122223333:key/abcd-1234-efgh-5678" 
      ]
    }
  ]
}
```
---
## Credenciais necessárias
- AWS_ACCESS_KEY - Chave de acesso da sua conta IAM 
- AWS_SECRET_KEY - Chave de secreta da sua conta IAM
- AWS_KMS_KEY - Chave gerada ao criar o KMS
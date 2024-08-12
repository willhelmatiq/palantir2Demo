When switching between the branches you have to reconfigure local terraform state with `-reconfigure` 

The backend is **DYNAMIC!**  
You have to provide configuration in a form:
`terraform init \
-backend-config="bucket=${TFSTATE_BUCKET}" \
-backend-config="prefix=${TFSTATE_KEY}"
`
e.g.

`terraform init \
-backend-config="bucket=cft-tfstate-5f33" \
-backend-config="prefix=terraform/devteam/palantir2/dev"`

State path is **DYNAMIC as well** and is computed as `terraform/devteam/$CI_PROJECT_NAME/$CI_COMMIT_REF_NAME`
For local build it should be convenient to set variables in terraform.tfvars (do not commit it, as it would be overwritten in the CI)
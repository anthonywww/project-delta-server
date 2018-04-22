# Contributing Guidelines
When contributing to this repository you agree to follow the following contributing guidelines.

## Commit message conventions
As it is not required to follow any convention for commit messages, we ask you to kindly make sure your commit messages are relevant and begin with an uppercase letter. The only exception would be for direct file names, which the extention of the file should be added unless the file itself does not have an extention such as the `LICENSE` file.

## Before making a pull-request
If you would like to submit a pull-request please ensure the following conditions are met before creating a PR:

1. Ensure any install or build dependencies are removed when doing a build. This includes temporary
   files and directories that are not directly required by the project.
2. Ensure the `.gitignore` is properly used.
3. Review your changes to ensure they will not intentionally cause harm (or a massive headache) to the project or other developers.

## Submitting a pull-request
The branches `master` and `testing` are protected branches where only a project maintainer may approve your pull-request. Follow the steps below on how to make a proper contribution.

1. You should be developing in your own branch (usually prefixed with `dev-`) and submit the PR to the `development` branch.
2. Be sure to label your pull-request accordingly for example, with the `bug` tag if the issue is bug related.
3. Leave the Assignee field empty unless told otherwise by a DevOps member.
4. Resolve any issues with the project manager assigned to your pull-request.
5. Once your branch has been pulled into `testing` it will be tested by a project maintainer, if it is stable it will be pulled into `master`.

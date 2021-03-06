Contributing to Apache SDAP 
===========================

Summary
-------
This document covers how to contribute to the SDAP project. SDAP uses github PRs to manage code contributions and project manages source code development through the [SDAP JIRA instance](https://issues.apache.org/jira/browse/SDAP). 
These instructions assume you have a GitHub.com account, so if you don't have one you will have to create one. Your proposed code changes will be published to your own fork of the SDAP project and you will submit a Pull Request for your changes to be added.

_Lets get started!!!_

Bug fixes
---------

It's very important that we can easily track bug fix commits, so their hashes should remain the same in all branches. 
Therefore, a pull request (PR) that fixes a bug, should be sent against a release branch. 
This can be either the "current release" or the "previous release", depending on which ones are maintained. 
Since the goal is a stable master, bug fixes should be "merged forward" to the next branch in order: "previous release" -> "current release" -> master (in other words: old to new)

Developing new features
-----------------------

Development should be done in a feature branch, branched off of master. 
Send a PR(steps below) to get it into master (2x LGTM applies). 
PR will only be merged when master is open, will be held otherwise until master is open again. 
No back porting / cherry-picking features to existing branches!

Fork the code 
-------------

In your browser, navigate to: [https://github.com/apache?utf8=✓&q=incubator-sdap&type=&language=](https://github.com/apache?utf8=✓&q=incubator-sdap&type=&language=)

Fork whichever repository you wish to contribute to by clicking on the 'Fork' button on the top right hand side. The fork will happen and you will be taken to your own fork of the repository.  Copy the Git repository URL by clicking on the clipboard next to the URL on the right hand side of the page under '**HTTPS** clone URL'.  You will paste this URL when doing the following `git clone` command.

On your computer, follow these steps to setup a local repository for working on ACS:

``` bash
$ git clone https://github.com/YOUR_ACCOUNT/incubator-sdap-mudrod.git
$ cd incubator-sdap-mudrod
$ git remote add upstream https://github.com/apache/incubator-sdap-mudrod.git
$ git checkout master
$ git fetch upstream
$ git rebase upstream/master
```

Making changes
--------------

It is important that you create a new branch to make changes on and that you do not change the `master` branch (other than to rebase in changes from `upstream/master`).  In this example we will assume you will be making your changes to a branch called `SDAP-XXX`.  This `SDAP-XXX` is named after the issue you have created within the [SDAP JIRA instance](https://issues.apache.org/jira/browse/SDAP). Therefore `SDAP-XXX` will be created on your local repository and will be pushed to your forked repository on GitHub.  Once this branch is on your fork you will create a Pull Request for the changes to be added to the SDAP project.

It is best practice to create a new branch each time you want to contribute to the project and only track the changes for that pull request in this branch.

``` bash
$ git checkout -b SDAP-XXX
   (make your changes)
$ git status
$ git add .
$ git commit -a -m "SDAP-XXX Descriptive title of SDAP-XXX"
```

> The `-b` specifies that you want to create a new branch called `SDAP-XXX`.  You only specify `-b` the first time you checkout because you are creating a new branch.  Once the `SDAP-XXX` branch exists, you can later switch to it with only `git checkout SDAP-XXX`.
> Note that the commit message comprises the JIRA issue number and title... this makes explicit reference between Github and JIRA for improved project management.


Rebase `SDAP-XXX` to include updates from `upstream/master`
------------------------------------------------------------

It is important that you maintain an up-to-date `master` branch in your local repository.  This is done by rebasing in the code changes from `upstream/master` (the official SDAP project repository) into your local repository.  You will want to do this before you start working on a feature as well as right before you submit your changes as a pull request.  We recommend you do this process periodically while you work to make sure you are working off the most recent project code.

This process will do the following:

1. Checkout your local `master` branch
2. Synchronize your local `master` branch with the `upstream/master` so you have all the latest changes from the project
3. Rebase the latest project code into your `SDAP-XXX` branch so it is up-to-date with the upstream code

``` bash
$ git checkout master
$ git fetch upstream
$ git rebase upstream/master
$ git checkout SDAP-XXX
$ git rebase master
```

> Now your `SDAP-XXX` branch is up-to-date with all the code in `upstream/master`.


Make a GitHub Pull Request to contribute your changes
-----------------------------------------------------

When you are happy with your changes and you are ready to contribute them, you will create a Pull Request on GitHub to do so. This is done by pushing your local changes to your forked repository (default remote name is `origin`) and then initiating a pull request on GitHub.

Please include JIRA id, detailed information about the bug/feature, what all tests are executed, how the reviewer can test this feature etc. Incase of UI PRs, a screenshot is preferred.

> **IMPORTANT:** Make sure you have rebased your `SDAP-XXX` branch to include the latest code from `upstream/master` _before_ you do this.

``` bash
$ git push origin master
$ git push origin SDAP-XXX
```

Now that the `SDAP-XXX` branch has been pushed to your GitHub repository, you can initiate the pull request.  

To initiate the pull request, do the following:

1. In your browser, navigate to your forked repository: [https://github.com/YOUR_ACCOUNT?utf8=✓&q=incubator-sdap&type=&language=](https://github.com/YOUR_ACCOUNT?utf8=✓&q=incubator-sdap&type=&language=), make sure you actually navigate to the specific project you wish to make the PR from.
2. Click the new button called '**Compare & pull request**' that showed up just above the main area in your forked repository
3. Validate the pull request will be into the upstream `master` and will be from your `SDAP-XXX` branch
4. Enter a detailed description of the work you have done and then click '**Send pull request**'

If you are requested to make modifications to your proposed changes, make the changes locally on your `SDAP-XXX` branch, re-push the `SDAP-XXX` branch to your fork.  The existing pull request should automatically pick up the change and update accordingly.


Cleaning up after a successful pull request
-------------------------------------------

Once the `SDAP-XXX` branch has been committed into the `upstream/master` branch, your local `SDAP-XXX` branch and the `origin/SDAP-XXX` branch are no longer needed.  If you want to make additional changes, restart the process with a new branch.

> **IMPORTANT:** Make sure that your changes are in `upstream/master` before you delete your `SDAP-XXX` and `origin/SDAP-XXX` branches!

You can delete these deprecated branches with the following:

``` bash
$ git checkout master
$ git branch -D SDAP-XXX
$ git push origin :SDAP-XXX
```

Release Principles
------------------
Coming soon

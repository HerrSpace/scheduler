#!/bin/bash

function quit() {
    echo "ERROR: $*"
    exit 1
}


function getVersion() {
    mvn ${MVN_ARGS} org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v "\[INFO\]"
}


    git config --global user.email "autobot@btrplace.org"
    git config --global user.name "Auto Bot"

    #Extract the version
    VERSION=$(getVersion)
    TAG="btrplace-scheduler-${VERSION}"
    COMMIT=$(git rev-parse HEAD)
    echo "** Starting the release of ${TAG} from ${COMMIT} **"
    #Quit if tag already exists
    git ls-remote --exit-code --tags origin ${TAG} && quit "tag ${TAG} already exists"

    #Working version ?
    mvn clean test ||quit "Unstable build"

    git fetch origin master:refs/remotes/origin/master||quit "Unable to fetch master"
    #Integrate with master and tag
    echo "** Integrate to master **"
    git checkout -b master origin/master||quit "No master branch"
    git merge -m "merging with version ${VERSION}" -s recursive -X theirs --no-ff ${COMMIT}||quit "Unable to integrate to master"

    #Javadoc
    ./bin/push_javadoc.sh apidocs.git ${VERSION}

    git tag ${TAG} ||quit "Unable to tag"
    git push --tags ||quit "Unable to push the tag"
    git push origin master ||quit "Unable to push master"

    #Deploy the artifacts
    echo "** Deploying the artifacts **"
    ./bin/deploy.sh ||quit "Unable to deploy"

    #Clean
    #git push origin --delete release

    #Set the next development version
    echo "** Prepare develop for the next version **"
    git fetch origin develop:refs/remotes/origin/develop||quit "Unable to fetch develop"
    git checkout -b develop origin/develop||quit "No develop branch"
    git merge -s recursive -X theirs -m "merging with version ${VERSION}" --no-ff ${TAG}
    ./bin/set_version.sh --next ${VERSION}
    git commit -m "Prepare the code for the next version" -a


    #Push changes on develop, with the tag
    git push origin develop ||echo "/!\ Unable to push develop"

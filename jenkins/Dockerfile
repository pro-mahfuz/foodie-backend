FROM docker:29-cli AS docker-cli

FROM jenkins/jenkins:2.568.2-jdk21

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends git maven \
    && rm -rf /var/lib/apt/lists/*
COPY --from=docker-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker-cli /usr/local/libexec/docker/cli-plugins /usr/local/libexec/docker/cli-plugins
RUN groupadd --gid 999 docker \
    && usermod --append --groups docker jenkins

USER jenkins
RUN jenkins-plugin-cli --plugins \
    workflow-aggregator \
    git \
    github-branch-source \
    credentials-binding \
    docker-workflow

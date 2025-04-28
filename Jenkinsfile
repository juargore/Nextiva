@Library('nextiva-pipeline') _

gitflowPipeline {

    project([ name: 'android' ])
    agent = 'android'

    projectScm {
        submodules = true
    }

    shell {
        stageName = 'Unit Tests'
        agent = [ useContainer: false ]
        script = 'cd fastlane && $HOME/.rbenv/shims/fastlane tests'
    }

    slack('#mobile-deployments')
}

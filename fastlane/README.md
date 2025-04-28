fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

### tests

```sh
[bundle exec] fastlane tests
```

Clean and run the unit tests against the Dev Release variant

### setup_package

```sh
[bundle exec] fastlane setup_package
```

Test credentials and package name

### deploy_nightly

```sh
[bundle exec] fastlane deploy_nightly
```

Clean, assemble, and then upload the Nightly Debug variant

`fastlane deploy_nightly seconds_since_epoch:{SECONDS_SINCE_EPOCH} store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### deploy_pr

```sh
[bundle exec] fastlane deploy_pr
```

Clean, assemble, and then upload the Deploy PR Debug variant

`fastlane deploy_pr store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### deploy_alpha

```sh
[bundle exec] fastlane deploy_alpha
```

Clean, assemble, and then upload the Alpha Debug variant

`fastlane deploy_alpha store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### deploy_labs

```sh
[bundle exec] fastlane deploy_labs
```

Clean, assemble, and then upload the Labs Debug variant

`fastlane deploy_labs store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### deploy_rc

```sh
[bundle exec] fastlane deploy_rc
```

Clean, assemble, and then upload the RC Debug variant

`fastlane deploy_rc store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### deploy_prod

```sh
[bundle exec] fastlane deploy_prod
```

Clean, assemble, and then upload the Prod Release variant

`fastlane deploy_prod store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

### veracode

```sh
[bundle exec] fastlane veracode
```

Clean and assemble a build for veracode

`fastlane veracode seconds_since_epoch:{SECONDS_SINCE_EPOCH} store_file:{PATH_TO_KEYSTORE} store_password:{KEYSTORE_PASSWORD} key_alias:{KEY_ALIAS} key_password:{KEY_PASSWORD}`

optionals: seconds_since_epoch:{SECONDS_SINCE_EPOCH} version_name:{VERSION_NAME}

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).

export RELEASE_FILE_VERSION=$(gradle properties | grep version: | cut -d ' ' -f 2)
export RELEASE_FILE_NAME="./application/build/libs/application-$RELEASE_FILE_VERSION.jar"
echo "RELEASE_FILE_VERSION: $RELEASE_FILE_VERSION"
echo "RELEASE_FILE_NAME: $RELEASE_FILE_NAME"
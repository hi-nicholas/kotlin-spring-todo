{ config, lib, pkgs, ... }:

{
  env = builtins.listToAttrs (map (var: {
    name = builtins.elemAt var 0;
    value = lib.maybeEnv (builtins.elemAt var 0) (builtins.elemAt var 1);
  }) [
    [ "APP_DB_USERNAME" "demo" ]
    [ "APP_DB_PASSWORD" "demo" ]
    [ "APP_DB_HOST" "127.0.0.1" ]
    [ "APP_DB_PORT" "5432" ]
    [ "APP_DB_DATABASE" "demo" ]
    [ "APP_DB_SCHEMA" "demo" ]
  ]);

  hi.package-info = [ pkgs.kotlin pkgs.postgresql pkgs.zulu ];

  languages = {
    java = {
      enable = true;
      gradle.enable = true;
      jdk.package = pkgs.zulu;
    };

    kotlin.enable = true;
  };

  scripts.get-name.exec =
    "./gradlew properties | grep '^name:' | sed 's/^name: //'";

  scripts.get-version.exec =
    "./gradlew properties | grep '^version:' | sed 's/^version: //'";

  scripts.get-jar-path.exec = ''
    echo "${config.env.DEVENV_ROOT}/build/libs/$(get-name)-$(get-version).jar"'';

  processes.kotlin-demo = {
    exec = "java -jar $(get-jar-path)";

    process-compose.depends_on.postgres.condition = "process_healthy";
  };

  services.postgres = {
    enable = true;

    port = lib.toInt config.env.APP_DB_PORT;

    initialDatabases = [{
      name = config.env.APP_DB_DATABASE;
      pass = config.env.APP_DB_PASSWORD;
      user = config.env.APP_DB_USERNAME;
    }];

    listen_addresses = config.env.APP_DB_HOST;
  };

  tasks = {
    "kotlin-demo:build" = {
      exec = ''
        cd ${config.env.DEVENV_ROOT} && ./gradlew assemble
      '';

      status = ''test -d "${config.env.DEVENV_ROOT}/build"'';
    };

    "devenv:enterShell".after = [ "kotlin-demo:build" ];
  };

  # See full reference at https://devenv.sh/reference/options/
}

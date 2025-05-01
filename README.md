# Demo - Java Securit

SonarQube Cloud Quality Gate Status:
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SonarCloud-Demos_demo-java-security&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SonarCloud-Demos_demo-java-security)

Code Coverage:
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SonarCloud-Demos_demo-java-security&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SonarCloud-Demos_demo-java-security)

SonarQube Cloud AI Code Assurance:
[![AI Code Assurance](https://sonarcloud.io/api/project_badges/ai_code_assurance?project=SonarCloud-Demos_demo-java-security)](https://sonarcloud.io/summary/new_code?id=SonarCloud-Demos_demo-java-security)

## Use case
This example demonstrates:
- Vulnerabilities
- Security Hotspots

It also demonstrates the possibility to define your own custom sources, sanitizers and sinks to detect more injection cases
(or avoid false positives)

## Usage

Run `./run.sh`

This will:
- Delete the project key **training:java-security** if it exists in SonarQube (to start from a scratch)
- Run `mvn clean verify sonar:sonar` to re-create the project

Project consists of a single class (`Insecure.java`) with a number of Vulnerabilities and Security Hotspots.

## Custom security configuration 
At the bottom of the class you see a bunch of methods that demonstrate custom injections.
- The method without sanitization (`doSomething()`) has an injection vulnerability
- The method with custom sanitization (`doSomethingSanitized()`) has no vulnerability

The custom security configuration file is in the root directory [here](s3649JavaSqlInjectionConfig.json)

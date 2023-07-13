JMLab OpenAI Java Client
========================
JMLab OpenAI Java Client is a Java library that provides the option for either synchronous or asynchronous requests to
OpenAI's API. Asynchronous requests are achieved through the use of Server-Sent Events (SSE) with the stream=true
option. This allows the client to receive results as soon as they become available, without blocking the thread, making
it useful for applications that require real-time updates, such as chat applications.

On the other hand, the synchronous option uses the stream=false option, which blocks until the results are available.

The library has a straightforward interface and is customizable, making it easy for developers to integrate OpenAI's AI
capabilities into their Java applications.

## version

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/kr.jmlab/openai-java-client/badge.svg)](http://search.maven.org/#artifactdetails%7Ckr.jmlab%7Copenai-java-client%7C0.1.2%7Cjar)

## Prerequisites:

* Java 11 or later

## Usage

Gradle:

```groovy
compile 'kr.jmlab:openai-java-client:0.1.2'
```

Maven:

```xml

<dependency>
    <groupId>kr.jmlab</groupId>
    <artifactId>openai-java-client</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Installation

Checkout the source code:

    git clone https://github.com/JM-Lab/openai-java-client.git
    cd openai-java-client
    git checkout -b 0.1.2 origin/0.1.2
    mvn install

## Features :

OpenAI Completions API Client
* **[OpenAiChatCompletions.java](https://github.com/JM-Lab/openai-java-client/tree/master/src/main/java/kr/jm/openai/OpenAiChatCompletions.java)**
  * [OpenAI Chat API](https://platform.openai.com/docs/api-reference/chat)
* **[OpenAiCompletions.java](https://github.com/JM-Lab/openai-java-client/tree/master/src/main/java/kr/jm/openai/OpenAiCompletions.java)**
  * [OpenAI Completions API](https://platform.openai.com/docs/api-reference/completions)
* **[GptTokenAnalyzer.java]([https://github.com/JM-Lab/openai-java-client/tree/master/src/main/java/kr/jm/openai/GptTokenAnalyzer.java](https://github.com/JM-Lab/openai-java-client/blob/master/src/main/java/kr/jm/openai/token/GptTokenAnalyzer.java))**
  * [jtokkit](https://github.com/knuddelsgmbh/jtokkit)

### For Example :

* **[TestCase](https://github.com/JM-Lab/openai-java-client/tree/master/src/test/java/kr/jm/openai)**

## LICENSE

Copyright 2023 Jemin Huh (JM)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

# 话匣子

我知道拼音拼错了, 暂时懒得改了。

[![Build Status](https://travis-ci.org/hanxiao/huajiazi.svg?branch=master)](https://travis-ci.org/hanxiao/huajiazi)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2ddf51266f9747049a7ae9c96cda871a)](https://www.codacy.com/app/artex-xh/huajiazi?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hanxiao/huajiazi&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/hanxiao/huajiazi/branch/master/graph/badge.svg)](https://codecov.io/gh/hanxiao/huajiazi)


## Install

In order to correctly parse the annotation in the code, you will need to install
`lombok plugin` in your IntelliJ.

Default port is `9090`.


## API Documentation

Question-Answer Pairs (QAP) are grouped by **topic**.

### To get available topics in the service, use
```
GET http://server:port/topics
```


### Check how many QAPs in a given topic, e.g. `phd`
```
GET http://server:port/phd/size
```

### To list all QAPs in a given topic, e.g. `phd`
- Get all QAPs: ```http://server:port/phd/list?filter=all```
- Get solved QAPs: ```http://server:port/phd/list?filter=solved```
- Get unsolved QAPs: ```http://server:port/phd/list?filter=unsolved```

### To ask a question, e.g. `你好`
```
GET http://server:port/phd?query=你的作者是
```
It will return
```json
{
  "question": "你的作者是谁啊?",
  "answer": "肖涵",
  "topic": "default",
  "didYouMean": [
    "你的作者是哪里人"
  ],
  "score": 0.21110917627811432,
  "hits": 1
}
```

### To teach a QAP for a given topic, use
```
POST http://server:port/teach
```
with the following payload:
```
{
    "question": "你的你的作者是哪里人",
    "answer": "北京",
    "topic": "phd"
}
```
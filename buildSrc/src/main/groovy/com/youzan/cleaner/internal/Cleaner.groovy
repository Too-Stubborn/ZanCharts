package com.youzan.cleaner.internal

public class Cleaner {
    def parse(File report) {
        def issues = new XmlParser().parse(report)

    }
}
package com.youzan.cleaner.internal

import groovy.xml.XmlUtil

public class Cleaner {
    def static clean(File report, Iterable<String> excludedFiles) {
        def issues = new XmlSlurper().parse(report)
        issues.'*'.findAll {
            it.name() == 'issue' && it.@id == 'UnusedResources'
        }.each {
            def file = new File(it.location.@file.text())
            if (file.name in excludedFiles) return;
            def line = it.location.@line
            def column = it.location.@column

            if ((line == '' && column == '') || column == '1') {
                println "deleting " + file.path
                file.delete()
            } else {
                def m = it.@message =~ $/`R.(\w+).([^`]+)`/$
                if (!m) return;

                def type = m.group(1)
                def entryName = m.group(2);

                def parsed = new XmlSlurper().parse(file)
                parsed.'**'.findAll {
                    it.@name == entryName && it.name().contains(type)
                }*.replaceNode {}

                XmlUtil.serialize(parsed, new FileWriter(file))
            }
        }
    }
}


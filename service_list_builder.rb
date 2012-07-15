#!/usr/bin/ruby
require 'open-uri'

url = "http://www.dns-sd.org/ServiceTypes.html"

data = nil
open(url) { |f| data = f.read() }

if data.nil?
    puts "Can't read url #{url}"
end

lines = data.split(/[\r\n]+/)

for line in lines
    match = line.match(/^<b>(.*?)<\/b>\s+(.*)/)
    if match
        puts "register(\"#{match[1]}\", \"#{match[2].gsub(/"/,"\\\"").gsub(/<.*?>/,'')}\", R.drawable.gear2);\n"
    end
end
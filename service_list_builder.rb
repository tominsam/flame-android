#!/usr/bin/ruby
require 'open-uri'
#require 'json'

url = "http://www.dns-sd.org/ServiceTypes.html"

data = nil
open(url) { |f| data = f.read() }

if data.nil?
    puts "Can't read url #{url}"
end

lines = data.split(/[\r\n]+/)

services = []

for line in lines
    match = line.match(/^<b>(.*?)<\/b>\s+(.*)/)
    services.push({
        "slug" => match[1],
        "name" => match[2],
    })
end

print services

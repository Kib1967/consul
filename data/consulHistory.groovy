import groovy.io.FileType
import groovy.json.JsonSlurper

import java.io.File

/////////////////////////////////////////////////////
// Things that know how to sort files
/////////////////////////////////////////////////////

def sortByTimestamp = { a, b ->
    a.lastModified() <=> b.lastModified()
}

def sortReverseByTimestamp = { a, b ->
    b.lastModified() <=> a.lastModified()
}

/////////////////////////////////////////////////////
// Things that know how to handle history files
/////////////////////////////////////////////////////

def decodeLabel = { json, html, previousKVs ->
	def result = false
	if(json.size()==1 && json.get(0).containsKey('Label')) {
		def label = json.get(0).get('Label')
		html {
			tr(class: 'active') {
				td(colspan: '4') { mkp.yield(label) }
			}
		}
		result = true
	}
	
	result
}

def decodeConsulProperties = { json, html, previousKVs ->

	// TODO can-handle check
	
	def previousKeys = new HashSet(previousKVs.keySet())
	def newKeys = []
	
	html {
		json.each { jsonObj ->
	
			newKeys.add jsonObj.Key
			
			def decodedValue = new String(jsonObj.Value.decodeBase64())
			def oldValue = previousKVs[jsonObj.Key]
		
			// Potential delta
			if(oldValue != null) {
				if(!decodedValue.equals(oldValue)) {
					tr(class: 'info') {
						td '\u0394' // Delta
						td jsonObj.Key
						td oldValue
						td decodedValue
					}
					previousKVs[jsonObj.Key] = decodedValue
				}
			}
			
			// Addition
			else {
				tr(class: 'success') {
					td '+'
					td jsonObj.Key
					td ''
					td decodedValue
				}
				previousKVs[jsonObj.Key] = decodedValue
			}
		}
		
		// Process deletions
		previousKeys.removeAll(newKeys)
		previousKeys.each {
			tr(class: 'warning') {
				td '-'
				td it
				td oldValue
				td ''
			}
			previousKVs.remove it
		}
	}
}

def decoders = [decodeLabel, decodeConsulProperties]

/////////////////////////////////////////////////////
// Main processing
/////////////////////////////////////////////////////

def previousKVs = [:]

html.html {
    head {
        title 'Property history'
		link(rel: 'stylesheet', href: './bootstrap.min.css', media: 'screen')
    }
    body {
		div(class: 'container') {
		
			h1 'Properties data history'
		
			table(class: 'table table-striped table-hover') {
				thead {
					tr {
						th '+/-/\u0394'
						th 'Key'
						th 'Old value'
						th 'New value'
					}
				}
				tbody {
					new File('/home/vagrant/consul_history').traverse(
						type: FileType.FILES,
						sort: sortByTimestamp
					) {
						def json = new JsonSlurper().parseText(it.text)
						
						def decoder = decoders.find {decoder -> decoder(json, html, previousKVs)}
						if(decoder==null) {
							mkp.yield("Warning - no decoder found for file ${it.name}")
						}
					}
				}
			}
		}
	}
}

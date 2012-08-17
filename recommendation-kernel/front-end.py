import sys
from lib import search
from BaseHTTPServer import BaseHTTPRequestHandler
from BaseHTTPServer import HTTPServer
from urlparse import urlparse
from urlparse import parse_qs


class MyHttpHandler(BaseHTTPRequestHandler):
	"""
		The http handler to process request.
	"""
	def do_GET(self):
		try:
			url = "http://com1384.eecs.utk.edu:808/"+str(self.path)
			structure = urlparse(url)
			print structure
			paras = parse_qs(structure.query)
			print paras
			if "hwid" in paras.keys():
				hwid = paras["hwid"]
			else:
				hwid = None
			if "topk" in paras.keys():
				topk = paras["topk"]
			else:
				topk = 0
			print hwid
			print topk
			search_engine = search.MySearchHelper()
			xml_result = search_engine.search(str(hwid[0]), int(topk[0]))
			search_engine.save_xml(xml_result, str(hwid[0])+".xml")
			
			f = open(str(hwid[0])+".xml", "r")
			self.send_response(200)
			self.send_header("Content-Type", "text/xml")
			self.wfile.write("\r\n")
			msg = f.read()
			print len(msg)
			#self.wfile.write("<html>")
			#self.wfile.write("<header \>")
			#self.wfile.write("<body>")
			#self.wfile.write("It works.")
			#self.wfile.write("</body>")
			self.wfile.write(msg)
			return
		except IOError:
			#self.send_error(404, "Error")
			pass


def main():
	try:
		server = HTTPServer(("com1384.eecs.utk.edu", 808), MyHttpHandler)
		server.serve_forever()
	except KeyboardInterrupt:
		server.socket.close()

if __name__ == "__main__":
	main()

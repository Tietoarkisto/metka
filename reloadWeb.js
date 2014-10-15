var dirs = [
	['metka/src/main/webapp/resources', 'metka/target/metka-0.0.1-SNAPSHOT/resources'],
	['metka/src/main/webapp/WEB-INF/inc', 'metka/target/metka-0.0.1-SNAPSHOT/WEB-INF/inc'],
	['metka/src/main/webapp/WEB-INF/jsp', 'metka/target/metka-0.0.1-SNAPSHOT/WEB-INF/jsp']
];

var fs = require('fs-extra');
(function each() {
	if (!dirs.length) {
		return;
	}
	
	var copy = dirs.shift();

	fs.copy(copy[0], copy[1], function (err) {
		if (err) {
			return console.error(err);
		}
		each();
	});
})();

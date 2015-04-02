var dirs = [
	['metka/src/main/webapp/resources', 'metka/target/metka/resources'],
	['metka/src/main/webapp/WEB-INF/inc', 'metka/target/metka/WEB-INF/inc'],
	['metka/src/main/webapp/WEB-INF/jsp', 'metka/target/metka/WEB-INF/jsp']
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

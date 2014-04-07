$(document).ready(function() {
    MetkaJS.ReferenceHandler = function() {
        function revisionableReferenceModelInput(key) {
            var request = new Object();
            request.key = key;
            request.id = MetkaJS.SingleObject.id;
            request.revision = MetkaJS.SingleObject.revision;

            $.ajax({
                type: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: "json",
                url: MetkaJS.PathBuilder().add("references").add("collectOptions").build(),
                data: JSON.stringify(request)
            }).done(function(data) {
                alert(JSON.stringify(data));
            });
        }

        function fillSelectionOptions(input, options, includeEmpty) {
            includeEmpty = (includeEmpty == null) ? true : includeEmpty;

        }

        return {
            handleRevisionableReferenceModelInput: revisionableReferenceModelInput
        }
    }();
});
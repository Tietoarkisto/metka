/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function (require) {
    'use strict';

    return function treeView(root, events) {

        function recursiveForEach(f) {
            root.forEach(function recur(node) {
                if (node.children) {
                    node.children.forEach(recur);
                }
                f.apply(this, arguments);
            });
        }

        function $dirItems($root) {
            var atNextSiblingDir = false;
            var level = $root.data('level');
            return $root.nextAll().filter(function () {
                if (atNextSiblingDir) {
                    return false;
                }
                if (level < $(this).data('level')) {
                    return true;
                }
                atNextSiblingDir = true;
            });
        }

        function activateNodes(node) {
            var nodes = [].concat( node );
            nodes.forEach(function(_node) {
                _node.active = true;
                $div.children().filter(function () {
                    return _node === $(this).data('node');
                }).addClass('active');
            })

        }

        function deactivateAll() {
            recursiveForEach(function (node) {
                node.active = false;
            });
            $div.children().removeClass('active');
        }

        function children(items, level) {
            return items.map(function (node) {

                var $icon = $('<span class="glyphicon">')
                    .addClass(node.children ? closed : 'glyphicon-file');

                var $a = $('<a class="list-group-item" href="javascript:void 0;">')
                    .toggleClass('active', !!node.active)
                    .append($icon)
                    .append(' ' + node.text)
                    .data({
                        level: level,
                        node: node
                    });
                $icon
                    .css('margin-left', (level * 16) + 'px');

                if (node.children) {
                    var $remove = $('<span class="glyphicon">').addClass(remove).addClass("pull-right");
                    $a.append($remove);
                    $remove.click(function() {
                        node.transferRow.removed = true;
                        /*$dirItems($a).remove();
                        $a.remove();*/

                        if(events) {
                            events.refresh();
                        }
                        return false;
                    });
                    $icon.click(function () {
                        if ($icon.hasClass(closed)) {
                            // open

                            $a.after(children(node.children, level + 1));
                        } else {
                            // close

                            $dirItems($a).remove();
                        }
                        $icon.toggleClass(closed);
                        $icon.toggleClass(open);
                        return false;
                    });
                }

                return $a;
            });
        }

        var open = 'glyphicon-minus-sign';
        var closed = 'glyphicon-plus-sign';
        var remove = 'glyphicon-remove-sign';

        function activeNodes() {
            var response = [];
            recursiveForEach(function (node) {
                if (node.active) {
                    response.push(node);
                }
            });
            return response;
        }

        var $div = $('<div class="list-group list-group-condensed">')
            .append(children(root, 0))
            .on({
                // IE triggers native activate/deactivate events. Must use different event names.
                metkaActivate: function () {
                    var $this = $(this);
                    $this
                        .addClass('active')
                        .data('node').active = true;

                    $this
                        .trigger('change');
                },
                metkaDectivate: function () {
                    var $this = $(this);
                    $this
                        .removeClass('active')
                        .data('node').active = false;

                    $this
                        .trigger('change');
                },
                toggle: function () {
                    var $this = $(this);
                    $this
                        .toggleClass('active')
                        .data('node').active = $this.hasClass('active');
                    $this.trigger('change');
                },
                // Handle study variable multiselect when holding down the shift key
               multiselect: function () {
                    // array of selected nodes
                    var nodes = activeNodes();
                    // object containing list of all HTML anchor elements on the table
                    var elements = $div.children();
                    // Loop trough anchor elements and compare with selected nodes
                    Object.keys(elements).forEach(function(key) {
                        var element = $(elements[key]);
                        nodes.forEach(function(node) {
                            // no unique reliable keys available for use at this point, so we use text
                            var varText = String(element[0].text).replace(/\s/g,'');
                            var nodeText = String(node.text).replace(/\s/g,'');
                            if(varText === nodeText){
                                // handle selection
                                element.addClass('active');
                                element.trigger('change');
                            }
                        });
                    });
                },
                deactivateDirectoriesAndToggle: function () {
                    var $this = $(this);
                    var nodeLevel = $this.data('level');

                    $this.siblings().filter(function () {
                        return !!$(this).data('node').children;
                    }).removeClass('active');

                    recursiveForEach(function (node) {
                        if (node.children) {
                            node.active = false;
                        }
                    });
                    $this.trigger('toggle');
                },
                activateOne: function () {
                    var $this = $(this);

                    deactivateAll();

                    $this.trigger('metkaActivate');
                }
            }, 'a')
            .data({
                deactivateAll: deactivateAll,
                remove: function () {
                    var removed = activeNodes();

                    // remove from DOM
                    $div.children('.active').remove();

                    //remove from data
                    root = root.filter(function recur(node) {
                        if (node.children) {
                            node.children = node.children.filter(recur);
                        }
                        return !node.active;
                    });

                    $div
                        .trigger('change');
                    if (events.onDragged) {
                        events.onDragged(removed);
                    }
                    return removed;
                },
                add: function (nodes) {
                    var addTo = root;
                    var path = [];
                    var parent;

                    if (root.some(function recur(node, i, array) {
                        var found = (function () {
                            if (node.active) {
                                if (node.children) {
                                    addTo = node.children;
                                    parent = node;
                                } else {
                                    addTo = array;
                                }
                                return true;
                            } else {
                                if (node.children) {
                                    if (node.children.some(recur)) {
                                        if (!parent) {
                                            parent = node;
                                        }
                                        return true;
                                    }
                                }
                                return false;
                            }
                        })();
                        if (found && node.children) {
                            path.push(node);
                        }
                        return found;
                    })) {
                        if (events.onDropped) {
                            events.onDropped(parent, nodes);
                        }

                        deactivateAll();
                        Array.prototype.push.apply(addTo, nodes);
                        if (path.length) {
                            path.reverse().forEach(function (node, i) {
                                var $icon = $div.children().filter(function () {
                                    return node === $(this).data('node');
                                }).children('span').first();

                                if ($icon.hasClass(closed)) {
                                    $icon.click();
                                } else {
                                    // if "folder" is already open, do "refresh"
                                    $icon.click().click();
                                }
                            });
                        } else {
                            $div.append(children(nodes, 0));
                        }
                    } else {
                        deactivateAll();
                        Array.prototype.push.apply(root, nodes);
                        $div.append(children(nodes, 0));
                    }

                    $div
                        .scrollTop($div.children('.active').offset().top - $div.offset().top + $div.scrollTop())
                        .trigger('change');
                },
                move: function move(to) {
                    to.data('add')($div.data('remove')());
                },
                moveDir: function (dir) {
                    var movedVars = activeNodes();
                    deactivateAll();
                    if(dir == 1) {
                        movedVars.reverse();
                    }
                    movedVars.forEach(function (moved) {
                        root.forEach(function (node) {
                            var movedIndex = node.children.indexOf(moved);
                            if (node.children && movedIndex > -1 && node.children[movedIndex + dir] != undefined &&
                                    movedVars.indexOf(node.children[movedIndex + dir] ) == -1) {
                                var finalVariables = [];

                                activateNodes(node.children[movedIndex]);
                                var movingVariable = ($div.data('remove')())[0];

                                activateNodes(node.children);
                                var otherVariables = ($div.data('remove')());

                                movedIndex += dir;
                                for (var i = 0; i < movedIndex; i++) {
                                    finalVariables.push(otherVariables.shift());
                                }
                                finalVariables[movedIndex] = movingVariable;
                                otherVariables.forEach(function (variable) {
                                    finalVariables.push(variable);
                                });
                                activateNodes(node);
                                $div.data('add')(finalVariables);
                                deactivateAll();
                            }
                        });
                    });

                    activateNodes(movedVars);
                    $div.trigger('change');
                },
                activeNodes: activeNodes
            });

        if (events.onClick) {
            $div.on('click', 'a', function (e) {
                var $this = $(this);
                var eventName = events.onClick($this.data('node'));
                if (eventName) {
                    $this.trigger(eventName);
                }
                return false;
            });
        }

        if (events.onChange) {
            $div.on('change', function () {
                events.onChange(activeNodes());
            })
        }

        return $div;
    }
});

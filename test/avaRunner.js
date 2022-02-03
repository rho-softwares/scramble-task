const test     = require('ava');
const scramble = require('./scramblefn.js');

test('1/3 Test', t => { t.true(scramble("rekqodlw", "world")); });

test('2/3 Test', t => { t.true(scramble("cedewaraaossoqqyt", "codewars")); });

test('3/3 Test', t => { t.false(scramble("katas", "steak")); });
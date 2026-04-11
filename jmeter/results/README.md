# JMeter Results

This folder stores artifacts from non-GUI JMeter runs:

- `results.jtl` - raw samples
- `summary.log` - aggregate metrics
- `dashboard/` - HTML report

Run command (after JMeter is installed):

```bash
jmeter -n \
  -t jmeter/async-metrics-load-test.jmx \
  -l jmeter/results/results.jtl \
  -j jmeter/results/summary.log \
  -e -o jmeter/results/dashboard
```

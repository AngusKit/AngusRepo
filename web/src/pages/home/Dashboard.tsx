import { Package, Download, Upload, HardDrive, TrendingUp, Activity, Clock, Package2, Cpu, MemoryStick, Network, Wifi, Shield, AlertTriangle, CheckCircle, XCircle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { Progress } from '@/components/ui/progress';
import { useNavigate } from 'react-router-dom';

// Dashboard component with navigation support
export function Dashboard() {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const handleNavigate = (page: string) => {
    navigate(`/${page}`);
  };
  const stats = [
    {
      title: t('stats.totalRepositories'),
      value: '18',
      change: '+3',
      trend: 'up',
      icon: Package,
      color: 'text-blue-600 dark:text-blue-400',
      bgColor: 'bg-blue-100 dark:bg-blue-900/30',
    },
    {
      title: t('stats.totalArtifacts'),
      value: '2,847',
      change: '+124',
      trend: 'up',
      icon: Package2,
      color: 'text-purple-600 dark:text-purple-400',
      bgColor: 'bg-purple-100 dark:bg-purple-900/30',
    },
    {
      title: t('stats.downloads'),
      value: '45.2K',
      change: '+18%',
      trend: 'up',
      icon: Download,
      color: 'text-green-600 dark:text-green-400',
      bgColor: 'bg-green-100 dark:bg-green-900/30',
    },
    {
      title: t('stats.storageUsed'),
      value: '128.5 GB',
      change: '+12.4 GB',
      trend: 'up',
      icon: HardDrive,
      color: 'text-orange-600 dark:text-orange-400',
      bgColor: 'bg-orange-100 dark:bg-orange-900/30',
    },
  ];

  const repositories = [
    { name: 'maven-releases', format: 'Maven', type: 'hosted', artifacts: 234, size: '24.5 GB', status: 'online' },
    { name: 'docker-registry', format: 'Docker', type: 'hosted', artifacts: 89, size: '42.3 GB', status: 'online' },
    { name: 'npm-private', format: 'NPM', type: 'hosted', artifacts: 456, size: '12.8 GB', status: 'online' },
    { name: 'maven-central', format: 'Maven', type: 'proxy', artifacts: 1234, size: '31.2 GB', status: 'online' },
    { name: 'pypi-public', format: 'PyPI', type: 'proxy', artifacts: 567, size: '18.7 GB', status: 'online' },
  ];

  const recentActivity = [
    { action: 'upload', user: 'Alex Chen', artifact: 'spring-boot-starter:2.7.0', repository: 'maven-releases', time: '2 min ago', icon: Upload },
    { action: 'download', user: 'Sarah Johnson', artifact: 'nginx:1.21', repository: 'docker-registry', time: '5 min ago', icon: Download },
    { action: 'upload', user: 'Mike Wilson', artifact: 'react:18.2.0', repository: 'npm-private', time: '12 min ago', icon: Upload },
    { action: 'download', user: 'Emily Brown', artifact: 'pandas:1.5.3', repository: 'pypi-public', time: '25 min ago', icon: Download },
    { action: 'upload', user: 'Tom Anderson', artifact: 'postgres:15', repository: 'docker-registry', time: '1 hour ago', icon: Upload },
  ];

  const storageByFormat = [
    { format: 'Maven', used: 55.7, total: 100, percentage: 56, color: 'bg-purple-500' },
    { format: 'Docker', used: 42.3, total: 100, percentage: 42, color: 'bg-blue-500' },
    { format: 'NPM', used: 12.8, total: 100, percentage: 13, color: 'bg-green-500' },
    { format: 'NuGet', used: 8.5, total: 100, percentage: 9, color: 'bg-indigo-500' },
    { format: 'PyPI', used: 18.7, total: 100, percentage: 19, color: 'bg-orange-500' },
    { format: 'Apt', used: 6.2, total: 100, percentage: 6, color: 'bg-yellow-500' },
    { format: 'Yum', used: 4.8, total: 100, percentage: 5, color: 'bg-red-500' },
  ];

  const systemMetrics = [
    { 
      name: 'CPU', 
      value: 45, 
      status: 'normal',
      icon: Cpu,
      color: 'text-blue-600 dark:text-blue-400',
      bgColor: 'bg-blue-100 dark:bg-blue-900/30',
      barColor: 'bg-blue-500'
    },
    { 
      name: t('dashboard.memory'), 
      value: 68, 
      status: 'warning',
      icon: MemoryStick,
      color: 'text-orange-600 dark:text-orange-400',
      bgColor: 'bg-orange-100 dark:bg-orange-900/30',
      barColor: 'bg-orange-500'
    },
    { 
      name: t('dashboard.disk'), 
      value: 82, 
      status: 'high',
      icon: HardDrive,
      color: 'text-red-600 dark:text-red-400',
      bgColor: 'bg-red-100 dark:bg-red-900/30',
      barColor: 'bg-red-500'
    },
    { 
      name: t('dashboard.network'), 
      value: 34, 
      status: 'normal',
      icon: Network,
      color: 'text-green-600 dark:text-green-400',
      bgColor: 'bg-green-100 dark:bg-green-900/30',
      barColor: 'bg-green-500'
    },
  ];

  const networkStats = [
    { label: t('dashboard.inboundTraffic'), value: '124.5 MB/s', trend: '+12%' },
    { label: t('dashboard.outboundTraffic'), value: '86.3 MB/s', trend: '+8%' },
    { label: t('dashboard.activeConnections'), value: '1,247', trend: '+5%' },
    { label: t('dashboard.requestsPerSecond'), value: '3,542', trend: '+15%' },
  ];

  const vulnerabilityData = [
    { severity: t('security.critical'), count: 3, color: 'text-red-600 dark:text-red-400', bgColor: 'bg-red-100 dark:bg-red-900/30', percentage: 12 },
    { severity: t('security.high'), count: 8, color: 'text-orange-600 dark:text-orange-400', bgColor: 'bg-orange-100 dark:bg-orange-900/30', percentage: 32 },
    { severity: t('security.medium'), count: 12, color: 'text-yellow-600 dark:text-yellow-400', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', percentage: 48 },
    { severity: t('security.low'), count: 2, color: 'text-blue-600 dark:text-blue-400', bgColor: 'bg-blue-100 dark:bg-blue-900/30', percentage: 8 },
  ];

  const affectedRepositories = [
    { name: 'docker-registry', vulnerabilities: 8, severity: 'high' },
    { name: 'maven-releases', vulnerabilities: 6, severity: 'medium' },
    { name: 'npm-private', vulnerabilities: 5, severity: 'medium' },
    { name: 'pypi-public', vulnerabilities: 4, severity: 'low' },
  ];

  const formatBadgeColor = (format: string) => {
    const colors: Record<string, string> = {
      'Maven': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'Docker': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'NPM': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'PyPI': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
    };
    return colors[format] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const typeBadgeColor = (type: string) => {
    const colors: Record<string, string> = {
      'hosted': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'proxy': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'group': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
    };
    return colors[type] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('dashboard.welcome')}</h1>
          <p className="text-gray-600 dark:text-gray-400">{t('dashboard.subtitle')}</p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title}>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                    <Icon className={`size-6 ${stat.color}`} />
                  </div>
                  <Badge variant="secondary" className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                    <TrendingUp className="size-3 mr-1" />
                    {stat.change}
                  </Badge>
                </div>
                <div>
                  <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{stat.title}</p>
                  <p className="text-2xl text-gray-900 dark:text-white">{stat.value}</p>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Storage Usage */}
        <Card>
          <CardHeader>
            <CardTitle>{t('dashboard.storageUsage')}</CardTitle>
            <CardDescription>{t('dashboard.storageByFormat')}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {storageByFormat.map((item) => (
              <div key={item.format}>
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm text-gray-700 dark:text-gray-300">{item.format}</span>
                  <span className="text-sm text-gray-600 dark:text-gray-400">{item.used} GB / {item.total} GB</span>
                </div>
                <Progress value={item.percentage} className="h-2" />
              </div>
            ))}
          </CardContent>
        </Card>

        {/* Repository Stats */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>{t('dashboard.systemMonitoring')}</CardTitle>
                <CardDescription>{t('dashboard.systemMonitoringDesc')}</CardDescription>
              </div>
              <Badge variant="secondary" className="bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300">
                {t('dashboard.healthy')}
              </Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* System Metrics */}
            <div className="space-y-3">
              {systemMetrics.map((metric) => {
                const Icon = metric.icon;
                return (
                  <div key={metric.name} className="flex items-center gap-3">
                    <div className={`p-2 rounded-lg ${metric.bgColor}`}>
                      <Icon className={`size-4 ${metric.color}`} />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{metric.name}</span>
                        <span className="text-sm text-gray-600 dark:text-gray-400">{metric.value}%</span>
                      </div>
                      <Progress value={metric.value} className={`h-2 ${metric.barColor}`} />
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Network Stats */}
            <div className="pt-4 border-t border-gray-200 dark:border-gray-700">
              <div className="flex items-center gap-2 mb-3">
                <Wifi className="size-4 text-blue-600 dark:text-blue-400" />
                <span className="text-sm text-gray-900 dark:text-white">{t('dashboard.networkStats')}</span>
              </div>
              <div className="grid grid-cols-2 gap-3">
                {networkStats.map((stat, index) => (
                  <div key={index} className="p-3 rounded-lg bg-gray-50 dark:bg-gray-800/50">
                    <p className="text-xs text-gray-600 dark:text-gray-400 mb-1">{stat.label}</p>
                    <div className="flex items-center justify-between">
                      <p className="text-sm text-gray-900 dark:text-white">{stat.value}</p>
                      <span className="text-xs text-green-600 dark:text-green-400">{stat.trend}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Vulnerability Statistics */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>{t('dashboard.vulnerabilityStats')}</CardTitle>
                <CardDescription>{t('dashboard.vulnerabilityStatsDesc')}</CardDescription>
              </div>
              <Button variant="ghost" size="sm" onClick={() => handleNavigate('security')}>
                <Shield className="mr-2 size-4" />
                {t('dashboard.viewAll')}
              </Button>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Total Vulnerabilities */}
            <div className="flex items-center justify-between p-4 rounded-lg bg-gray-50 dark:bg-gray-800/50">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">{t('dashboard.totalVulnerabilities')}</p>
                <p className="text-2xl text-gray-900 dark:text-white">25</p>
              </div>
              <div className="flex items-center gap-4 text-sm">
                <div>
                  <p className="text-gray-600 dark:text-gray-400">{t('dashboard.recentlyFixed')}</p>
                  <p className="text-green-600 dark:text-green-400">12</p>
                </div>
                <div>
                  <p className="text-gray-600 dark:text-gray-400">{t('dashboard.fixRate')}</p>
                  <p className="text-blue-600 dark:text-blue-400">48%</p>
                </div>
              </div>
            </div>

            {/* Severity Distribution */}
            <div>
              <p className="text-sm text-gray-900 dark:text-white mb-3">{t('dashboard.severityDistribution')}</p>
              <div className="space-y-3">
                {vulnerabilityData.map((item) => (
                  <div key={item.severity} className="flex items-center gap-3">
                    <div className={`flex items-center justify-center size-8 rounded ${item.bgColor}`}>
                      <AlertTriangle className={`size-4 ${item.color}`} />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-sm text-gray-700 dark:text-gray-300">{item.severity}</span>
                        <span className="text-sm text-gray-600 dark:text-gray-400">{item.count}</span>
                      </div>
                      <div className="h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                        <div 
                          className={`h-full ${item.bgColor.replace('100', '500').replace('900/30', '500')}`}
                          style={{ width: `${item.percentage}%` }}
                        />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>{t('dashboard.recentActivity')}</CardTitle>
                <CardDescription>{t('dashboard.latestOperations')}</CardDescription>
              </div>
              <Button variant="ghost" size="sm" onClick={() => handleNavigate('activity-log')}>
                <Activity className="mr-2 size-4" />
                {t('dashboard.viewAll')}
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentActivity.map((activity, index) => {
                const Icon = activity.icon;
                const isUpload = activity.action === 'upload';
                return (
                  <div key={index} className="flex items-start gap-4 pb-4 border-b border-gray-200 dark:border-gray-700 last:border-0 last:pb-0">
                    <div className={`p-2 rounded-lg ${isUpload ? 'bg-blue-100 dark:bg-blue-900/30' : 'bg-green-100 dark:bg-green-900/30'}`}>
                      <Icon className={`size-4 ${isUpload ? 'text-blue-600 dark:text-blue-400' : 'text-green-600 dark:text-green-400'}`} />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-gray-900 dark:text-white">
                        <span className="font-medium">{activity.user}</span> {isUpload ? 'uploaded' : 'downloaded'}{' '}
                        <span className="font-mono text-purple-600 dark:text-purple-400">{activity.artifact}</span>
                      </p>
                      <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">
                        Repository: {activity.repository}
                      </p>
                    </div>
                    <div className="flex items-center gap-1 text-xs text-gray-500 dark:text-gray-400">
                      <Clock className="size-3" />
                      {activity.time}
                    </div>
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
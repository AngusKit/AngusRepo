import { useState } from 'react';
import { 
  Activity, Download, Upload, Trash2, Edit, Search, Filter, Calendar, User, Package,
  UserPlus, UserMinus, Settings, Shield, Key, Database, GitBranch, Lock, Unlock
} from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card'; 
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';

export function ActivityLog() {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [filterAction, setFilterAction] = useState('all');
  const [filterUser, setFilterUser] = useState('all');
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const activities = [
    // Artifact operations
    {
      id: '1',
      action: 'upload',
      user: 'Alex Chen',
      artifact: 'spring-boot-starter-web:3.1.5',
      repository: 'maven-releases',
      timestamp: '2024-01-19 14:32:15',
      ipAddress: '192.168.1.100',
      userAgent: 'Maven/3.9.0',
      details: 'Uploaded via Maven deploy plugin',
      category: 'artifact'
    },
    {
      id: '2',
      action: 'download',
      user: 'Sarah Johnson',
      artifact: 'nginx:1.21-alpine',
      repository: 'docker-registry',
      timestamp: '2024-01-19 14:28:42',
      ipAddress: '192.168.1.101',
      userAgent: 'Docker/24.0.0',
      details: 'Docker pull command',
      category: 'artifact'
    },
    // Repository operations
    {
      id: '3',
      action: 'create',
      user: 'John Anderson',
      artifact: 'maven-snapshots',
      repository: 'System',
      timestamp: '2024-01-19 14:25:10',
      ipAddress: '192.168.1.105',
      userAgent: 'Mozilla/5.0',
      details: 'Created new Maven repository',
      category: 'repository'
    },
    {
      id: '4',
      action: 'update',
      user: 'Emily Brown',
      artifact: 'docker-registry',
      repository: 'System',
      timestamp: '2024-01-19 14:20:33',
      ipAddress: '192.168.1.103',
      userAgent: 'Mozilla/5.0',
      details: 'Updated repository configuration',
      category: 'repository'
    },
    // User management operations
    {
      id: '5',
      action: 'user_add',
      user: 'John Anderson',
      artifact: 'mike.wilson@company.com',
      repository: 'System',
      timestamp: '2024-01-19 14:15:20',
      ipAddress: '192.168.1.105',
      userAgent: 'Mozilla/5.0',
      details: 'Added new team member with Developer role',
      category: 'user'
    },
    {
      id: '6',
      action: 'user_remove',
      user: 'Sarah Johnson',
      artifact: 'old.user@company.com',
      repository: 'System',
      timestamp: '2024-01-19 14:10:05',
      ipAddress: '192.168.1.101',
      userAgent: 'Mozilla/5.0',
      details: 'Removed inactive team member',
      category: 'user'
    },
    {
      id: '7',
      action: 'role_change',
      user: 'John Anderson',
      artifact: 'sarah.johnson@company.com',
      repository: 'System',
      timestamp: '2024-01-19 14:05:50',
      ipAddress: '192.168.1.105',
      userAgent: 'Mozilla/5.0',
      details: 'Changed role from Developer to Administrator',
      category: 'user'
    },
    // Security operations
    {
      id: '8',
      action: 'scan',
      user: 'System',
      artifact: 'spring-boot-starter-web:3.1.5',
      repository: 'maven-releases',
      timestamp: '2024-01-19 14:00:12',
      ipAddress: 'System',
      userAgent: 'Security Scanner',
      details: 'Completed security scan - 2 vulnerabilities found',
      category: 'security'
    },
    {
      id: '9',
      action: 'access_grant',
      user: 'John Anderson',
      artifact: 'docker-registry',
      repository: 'System',
      timestamp: '2024-01-19 13:55:38',
      ipAddress: '192.168.1.105',
      userAgent: 'Mozilla/5.0',
      details: 'Granted read access to Development team',
      category: 'security'
    },
    {
      id: '10',
      action: 'access_revoke',
      user: 'Sarah Johnson',
      artifact: 'npm-private',
      repository: 'System',
      timestamp: '2024-01-19 13:50:22',
      ipAddress: '192.168.1.101',
      userAgent: 'Mozilla/5.0',
      details: 'Revoked write access from External team',
      category: 'security'
    },
    // Cleanup operations
    {
      id: '11',
      action: 'cleanup',
      user: 'System',
      artifact: 'Clean Old Snapshots',
      repository: 'maven-snapshots',
      timestamp: '2024-01-19 13:45:15',
      ipAddress: 'System',
      userAgent: 'Cleanup Service',
      details: 'Cleanup policy executed - 45 items deleted, 2.3 GB reclaimed',
      category: 'cleanup'
    },
    // Settings operations
    {
      id: '12',
      action: 'settings_update',
      user: 'John Anderson',
      artifact: 'System Settings',
      repository: 'System',
      timestamp: '2024-01-19 13:40:08',
      ipAddress: '192.168.1.105',
      userAgent: 'Mozilla/5.0',
      details: 'Updated system configuration settings',
      category: 'settings'
    },
    // Recent artifact operations
    {
      id: '13',
      action: 'upload',
      user: 'Mike Wilson',
      artifact: 'react:18.2.0',
      repository: 'npm-private',
      timestamp: '2024-01-19 13:35:20',
      ipAddress: '192.168.1.102',
      userAgent: 'npm/9.5.0',
      details: 'Published via npm publish',
      category: 'artifact'
    },
    {
      id: '14',
      action: 'delete',
      user: 'Emily Brown',
      artifact: 'old-package:1.0.0',
      repository: 'npm-private',
      timestamp: '2024-01-19 13:30:15',
      ipAddress: '192.168.1.103',
      userAgent: 'Mozilla/5.0',
      details: 'Deleted obsolete package',
      category: 'artifact'
    },
    {
      id: '15',
      action: 'download',
      user: 'Tom Anderson',
      artifact: 'pandas:2.1.4',
      repository: 'pypi-public',
      timestamp: '2024-01-19 13:25:05',
      ipAddress: '192.168.1.104',
      userAgent: 'pip/23.3.1',
      details: 'Downloaded via pip install',
      category: 'artifact'
    },
  ];

  const actionIcon = (action: string) => {
    switch (action) {
      case 'upload':
        return <Upload className="size-4" />;
      case 'download':
        return <Download className="size-4" />;
      case 'delete':
        return <Trash2 className="size-4" />;
      case 'update':
        return <Edit className="size-4" />;
      case 'create':
        return <Database className="size-4" />;
      case 'user_add':
        return <UserPlus className="size-4" />;
      case 'user_remove':
        return <UserMinus className="size-4" />;
      case 'role_change':
        return <Shield className="size-4" />;
      case 'scan':
        return <Shield className="size-4" />;
      case 'access_grant':
        return <Unlock className="size-4" />;
      case 'access_revoke':
        return <Lock className="size-4" />;
      case 'cleanup':
        return <Trash2 className="size-4" />;
      case 'settings_update':
        return <Settings className="size-4" />;
      default:
        return <Activity className="size-4" />;
    }
  };

  const actionBadgeColor = (action: string) => {
    const colors: Record<string, string> = {
      'upload': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      'download': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'delete': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'update': 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      'create': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'user_add': 'bg-cyan-100 text-cyan-700 dark:bg-cyan-900/30 dark:text-cyan-300',
      'user_remove': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'role_change': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-300',
      'scan': 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-300',
      'access_grant': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      'access_revoke': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300',
      'cleanup': 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300',
      'settings_update': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300',
    };
    return colors[action] || 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300';
  };

  const filteredActivities = activities.filter(activity => {
    const matchesSearch = activity.artifact.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         activity.user.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         activity.repository.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesAction = filterAction === 'all' || activity.action === filterAction;
    const matchesUser = filterUser === 'all' || activity.user === filterUser;
    return matchesSearch && matchesAction && matchesUser;
  });

  const uniqueUsers = Array.from(new Set(activities.map(a => a.user)));

  const totalPages = Math.ceil(filteredActivities.length / itemsPerPage);
  const currentActivities = filteredActivities.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl text-gray-900 dark:text-white mb-2">{t('activityLog.title')}</h1>
        <p className="text-gray-600 dark:text-gray-400">{t('activityLog.description')}</p>
      </div>

      {/* Search and Filters */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="flex-1 relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
          <Input
            placeholder={t('activityLog.searchLogs')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
        <Select value={filterAction} onValueChange={setFilterAction}>
          <SelectTrigger className="w-full sm:w-48">
            <Filter className="mr-2 size-4" />
            <SelectValue placeholder={t('activityLog.action')} />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">{t('activityLog.allActions')}</SelectItem>
            <SelectItem value="upload">{t('activityLog.upload')}</SelectItem>
            <SelectItem value="download">{t('activityLog.download')}</SelectItem>
            <SelectItem value="delete">{t('activityLog.delete')}</SelectItem>
            <SelectItem value="update">{t('activityLog.update')}</SelectItem>
            <SelectItem value="create">{t('activityLog.create')}</SelectItem>
            <SelectItem value="user_add">{t('activityLog.userAdd')}</SelectItem>
            <SelectItem value="user_remove">{t('activityLog.userRemove')}</SelectItem>
            <SelectItem value="role_change">{t('activityLog.roleChange')}</SelectItem>
            <SelectItem value="scan">{t('activityLog.scan')}</SelectItem>
            <SelectItem value="access_grant">{t('activityLog.accessGrant')}</SelectItem>
            <SelectItem value="access_revoke">{t('activityLog.accessRevoke')}</SelectItem>
            <SelectItem value="cleanup">{t('activityLog.cleanup')}</SelectItem>
            <SelectItem value="settings_update">{t('activityLog.settingsUpdate')}</SelectItem>
          </SelectContent>
        </Select>
        <Select value={filterUser} onValueChange={setFilterUser}>
          <SelectTrigger className="w-full sm:w-40">
            <User className="mr-2 size-4" />
            <SelectValue placeholder={t('activityLog.user')} />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">{t('activityLog.allUsers')}</SelectItem>
            {uniqueUsers.map(user => (
              <SelectItem key={user} value={user}>{user}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Button variant="outline">
          <Download className="mr-2 size-4" />
          {t('activityLog.exportLogs')}
        </Button>
      </div>

      {/* Activity Timeline */}
      <Card>
        <CardContent className="p-0">
          <div className="divide-y divide-gray-200 dark:divide-gray-700">
            {currentActivities.map((activity) => (
              <div
                key={activity.id}
                className="p-4 hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
              >
                <div className="flex items-start gap-4">
                  <div className={`p-2 rounded-lg ${
                    activity.action === 'upload' ? 'bg-blue-100 dark:bg-blue-900/30' :
                    activity.action === 'download' ? 'bg-green-100 dark:bg-green-900/30' :
                    activity.action === 'delete' ? 'bg-red-100 dark:bg-red-900/30' :
                    'bg-gray-100 dark:bg-gray-800'
                  }`}>
                    {actionIcon(activity.action)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-2 flex-wrap">
                      <Badge className={actionBadgeColor(activity.action)}>
                        {activity.action.replace('_', ' ').toUpperCase()}
                      </Badge>
                      <span className="text-sm text-gray-900 dark:text-white font-medium">
                        {activity.user}
                      </span>
                      <span className="text-sm text-gray-600 dark:text-gray-400">
                        {activity.action === 'upload' ? 'uploaded' :
                         activity.action === 'download' ? 'downloaded' :
                         activity.action === 'delete' ? 'deleted' :
                         activity.action === 'create' ? 'created' :
                         activity.action === 'update' ? 'updated' :
                         activity.action === 'user_add' ? 'added user' :
                         activity.action === 'user_remove' ? 'removed user' :
                         activity.action === 'role_change' ? 'changed role for' :
                         activity.action === 'scan' ? 'scanned' :
                         activity.action === 'access_grant' ? 'granted access to' :
                         activity.action === 'access_revoke' ? 'revoked access from' :
                         activity.action === 'cleanup' ? 'executed cleanup' :
                         activity.action === 'settings_update' ? 'updated' :
                         'modified'}
                      </span>
                      <code className="text-sm font-mono text-purple-600 dark:text-purple-400">
                        {activity.artifact}
                      </code>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3 text-sm text-gray-600 dark:text-gray-400">
                      <div className="flex items-center gap-1">
                        <Package className="size-4 flex-shrink-0" />
                        <span className="truncate">{activity.repository}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <Calendar className="size-4 flex-shrink-0" />
                        <span>{activity.timestamp}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <span className="text-xs">IP:</span>
                        <span className="font-mono text-xs">{activity.ipAddress}</span>
                      </div>
                      <div className="flex items-center gap-1 truncate">
                        <span className="text-xs">Agent:</span>
                        <span className="font-mono text-xs truncate">{activity.userAgent}</span>
                      </div>
                    </div>
                    {activity.details && (
                      <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
                        {activity.details}
                      </p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {filteredActivities.length === 0 && (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <Activity className="size-12 text-gray-400 mb-4" />
            <h3 className="text-lg text-gray-900 dark:text-white mb-2">{t('activityLog.noLogsFound')}</h3>
            <p className="text-gray-600 dark:text-gray-400">{t('activityLog.tryAdjustFilters')}</p>
          </CardContent>
        </Card>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-4">
          <div className="text-sm text-gray-600 dark:text-gray-400">
            {t('activityLog.showing')} {((currentPage - 1) * itemsPerPage) + 1} {t('activityLog.to')} {Math.min(currentPage * itemsPerPage, filteredActivities.length)} {t('activityLog.of')} {filteredActivities.length} {t('activityLog.logs')}
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
              disabled={currentPage === 1}
            >
              {t('activityLog.previous')}
            </Button>
            <div className="flex items-center gap-1">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                <Button
                  key={page}
                  variant={page === currentPage ? "default" : "outline"}
                  size="sm"
                  onClick={() => setCurrentPage(page)}
                  className="min-w-[40px]"
                >
                  {page}
                </Button>
              ))}
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
              disabled={currentPage === totalPages}
            >
              {t('activityLog.next')}
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}